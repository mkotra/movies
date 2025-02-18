package pl.mkotra.movies.controller.ratelimitter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateLimiterFilterTest {

    private RateLimiterFilter rateLimiterFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        RateLimiterProperties rateLimiterProperties = mock(RateLimiterProperties.class);
        when(rateLimiterProperties.getMaxRequests()).thenReturn(5);
        when(rateLimiterProperties.getTimeWindow()).thenReturn(TimeUnit.MINUTES.toMillis(1));

        rateLimiterFilter = new RateLimiterFilter(rateLimiterProperties);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void doFilterInternal_whenUserIsNotAuthenticated() throws ServletException, IOException {
        //given
        SecurityContextHolder.getContext().setAuthentication(null);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenRateLimitIsExceeded() throws ServletException, IOException {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserRequest userRequest = new UserRequest();
        userRequest.setRequestCount(5);
        userRequest.setLastRequestTime(System.currentTimeMillis() - 1000);
        rateLimiterFilter.getUserRequests().put("user", userRequest);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        assertThat(userRequest.getRequestCount()).isEqualTo(5);
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(writer).write("Too many requests. Please try again later.");
    }

    @Test
    void doFilterInternal_whenRateLimitIsNotExceeded() throws ServletException, IOException {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserRequest userRequest = new UserRequest();
        userRequest.setRequestCount(3);
        userRequest.setLastRequestTime(System.currentTimeMillis() - 1000);
        rateLimiterFilter.getUserRequests().put("user", userRequest);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
        assertThat(userRequest.getRequestCount()).isEqualTo(4);
    }

    @Test
    void doFilterInternal_whenTimeWindowHasPassed() throws ServletException, IOException {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserRequest userRequest = new UserRequest();
        userRequest.setRequestCount(3);
        userRequest.setLastRequestTime(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2)); // Time window expired
        rateLimiterFilter.getUserRequests().put("user", userRequest);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
        assertThat(userRequest.getRequestCount()).isEqualTo(1);
    }
}
