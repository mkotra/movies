package pl.mkotra.movies.controller.ratelimitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateLimiterFilterTest {

    private static final String RESPONSE_BODY = "RESPONSE_BODY";

    private RateLimiterFilter rateLimiterFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private MeterRegistry meterRegistry;
    private ObjectMapper objectMapper;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {

        RateLimiterProperties rateLimiterProperties = mock(RateLimiterProperties.class);
        when(rateLimiterProperties.getMaxRequests()).thenReturn(5);
        when(rateLimiterProperties.getTimeWindow()).thenReturn(TimeUnit.MINUTES.toMillis(1));
        meterRegistry = mock(MeterRegistry.class);
        objectMapper = mock(ObjectMapper.class);

        rateLimiterFilter = new RateLimiterFilter(rateLimiterProperties, meterRegistry, objectMapper);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        when(objectMapper.writeValueAsString(any(ProblemDetail.class))).thenReturn(RESPONSE_BODY);
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

        UserRequest userRequest = new UserRequest(System.currentTimeMillis() - 1000);
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        rateLimiterFilter.getUserRequests().put("user", userRequest);

        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(mockCounter);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(writer).write(RESPONSE_BODY);
        verify(meterRegistry).counter(anyString(), any(String[].class));

        int requestCount = rateLimiterFilter.getUserRequests().get("user").getRequestCount();
        assertThat(requestCount).isEqualTo(5);
    }

    @Test
    void doFilterInternal_whenRateLimitIsNotExceeded() throws ServletException, IOException {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserRequest userRequest = new UserRequest(System.currentTimeMillis() - 1000);
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        rateLimiterFilter.getUserRequests().put("user", userRequest);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
        int requestCount = rateLimiterFilter.getUserRequests().get("user").getRequestCount();
        assertThat(requestCount).isEqualTo(4);
    }

    @Test
    void doFilterInternal_whenTimeWindowHasPassed() throws ServletException, IOException {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserRequest userRequest = new UserRequest(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2));
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        userRequest.incrementRequestCount();
        rateLimiterFilter.getUserRequests().put("user", userRequest);

        //when
        rateLimiterFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
        int requestCount = rateLimiterFilter.getUserRequests().get("user").getRequestCount();
        assertThat(requestCount).isEqualTo(0);
    }
}
