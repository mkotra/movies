package pl.mkotra.movies.controller.ratelimitter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterProperties rateLimiterProperties;
    private final ConcurrentHashMap<String, UserRequest> userRequests = new ConcurrentHashMap<>();

    RateLimiterFilter(RateLimiterProperties rateLimiterProperties) {
        this.rateLimiterProperties = rateLimiterProperties;
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
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = authentication.getName();
        UserRequest userRequest = userRequests.getOrDefault(username, new UserRequest());
        long currentTime = System.currentTimeMillis();

        if (userRequest.getRequestCount() >= maxRequestsPerTimeWindow && currentTime - userRequest.getLastRequestTime() < timeWindowMs) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        if (currentTime - userRequest.getLastRequestTime() > timeWindowMs) {
            userRequest.reset(currentTime);
        }

        userRequest.incrementRequestCount();
        userRequests.put(username, userRequest);

        filterChain.doFilter(request, response);
    }
}
