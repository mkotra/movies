package pl.mkotra.movies.controller.ratelimitter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private MeterRegistry meterRegistry;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {

        RateLimiterProperties rateLimiterProperties = mock(RateLimiterProperties.class);
        when(rateLimiterProperties.getMaxRequests()).thenReturn(5);
        when(rateLimiterProperties.getTimeWindow()).thenReturn(TimeUnit.MINUTES.toMillis(1));
        meterRegistry = mock(MeterRegistry.class);

        rateLimiterFilter = new RateLimiterFilter(rateLimiterProperties, meterRegistry);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
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

        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(mockCounter);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        assertThat(userRequest.getRequestCount()).isEqualTo(5);
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(writer).write("Too many requests for user user - please try again later.");
        verify(meterRegistry).counter(anyString(), any(String[].class));
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
