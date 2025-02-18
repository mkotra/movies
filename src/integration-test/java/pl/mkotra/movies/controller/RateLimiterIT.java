package pl.mkotra.movies.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.mkotra.movies.BaseIT;
import pl.mkotra.movies.controller.ratelimitter.RateLimiterProperties;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RateLimiterIT extends BaseIT {

    @MockitoBean
    RateLimiterProperties rateLimiterProperties;

    @BeforeEach
    void setup() {
        //just one request allowed with a huge time window
        when(rateLimiterProperties.getMaxRequests()).thenReturn(1);
        when(rateLimiterProperties.getTimeWindow()).thenReturn(60000L);
    }

    @Test
    void shouldLimitRequests() throws Exception {
        mockMvc.perform(get("/movies")
                        .with(httpBasic(USER, PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/movies")
                        .with(httpBasic(USER, PASSWORD)))
                .andDo(print())
                .andExpect(status().isTooManyRequests());
    }
}
