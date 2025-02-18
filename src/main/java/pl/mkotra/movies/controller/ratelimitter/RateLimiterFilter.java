package pl.mkotra.movies.controller.ratelimitter;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterFilter.class);

    private final RateLimiterProperties rateLimiterProperties;
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, UserRequest> userRequests = new ConcurrentHashMap<>();

    RateLimiterFilter(RateLimiterProperties rateLimiterProperties, MeterRegistry meterRegistry) {
        this.rateLimiterProperties = rateLimiterProperties;
        this.meterRegistry = meterRegistry;
    }

    public ConcurrentHashMap<String, UserRequest> getUserRequests() {
        return userRequests;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        int maxRequestsPerTimeWindow = rateLimiterProperties.getMaxRequests();
        long timeWindowMs = rateLimiterProperties.getTimeWindow();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = Optional.ofNullable(authentication.getName()).orElse("anonymousUser");

        UserRequest userRequest = userRequests.getOrDefault(username, new UserRequest());
        long currentTime = System.currentTimeMillis();

        if (userRequest.getRequestCount() >= maxRequestsPerTimeWindow && currentTime - userRequest.getLastRequestTime() < timeWindowMs) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            String message = "Too many requests for user " + username + " - please try again later.";
            response.getWriter().write(message);
            logger.warn(message);
            meterRegistry.counter("rate_limited_request_count").increment();
            return;
        }

        if (currentTime - userRequest.getLastRequestTime() > timeWindowMs) {
            userRequest.reset(currentTime);
        }

        userRequest.incrementRequestCount();
        userRequests.put(username, userRequest);

        filterChain.doFilter(request, response);
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !(requestURI.startsWith("/movies") || requestURI.startsWith("/actors"));
    }
}
