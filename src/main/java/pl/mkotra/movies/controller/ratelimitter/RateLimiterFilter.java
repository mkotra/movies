package pl.mkotra.movies.controller.ratelimitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterFilter.class);

    private final RateLimiterProperties rateLimiterProperties;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, UserRequest> userRequests = new ConcurrentHashMap<>();

    RateLimiterFilter(RateLimiterProperties rateLimiterProperties, MeterRegistry meterRegistry,
                      ObjectMapper objectMapper) {
        this.rateLimiterProperties = rateLimiterProperties;
        this.meterRegistry = meterRegistry;
        this.objectMapper = objectMapper;
    }

    public ConcurrentHashMap<String, UserRequest> getUserRequests() {
        return userRequests;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = Optional.ofNullable(authentication)
                .map(Authentication::getName)
                .orElse("anonymousUser");

        int maxRequestsPerTimeWindow = rateLimiterProperties.getMaxRequests();
        long timeWindowMs = rateLimiterProperties.getTimeWindow();
        long currentTime = System.currentTimeMillis();

        userRequests.compute(username, (_, userRequest) -> {
            if (userRequest == null || (currentTime - userRequest.getLastRequestTime()) > timeWindowMs) {
                return new UserRequest(currentTime);
            }
            if (userRequest.getRequestCount() >= maxRequestsPerTimeWindow) {
                return userRequest;
            }
            userRequest.incrementRequestCount();
            return userRequest;
        });

        UserRequest userRequest = userRequests.get(username);

        if (userRequest.getRequestCount() >= maxRequestsPerTimeWindow && currentTime - userRequest.getLastRequestTime() < timeWindowMs) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            String detail = "Too many requests for user " + username + " - please try again later.";
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, detail);
            problemDetail.setTitle("Too Many Requests");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(objectMapper.writeValueAsString(problemDetail));
                writer.flush();
            }
            logger.warn(detail);
            meterRegistry.counter("rate.limited.request.count").increment();
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !(requestURI.startsWith("/movies") || requestURI.startsWith("/actors"));
    }
}
