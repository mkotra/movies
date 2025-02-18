package pl.mkotra.movies.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.mkotra.movies.BaseIT;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RateLimiterIT extends BaseIT {

    @Test
    void shouldLimitRequests() throws Exception {
        mockMvc.perform(get("/movies")
                .header("Authorization", "Basic " + encodeCredentials(USER, PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/movies")
                .header("Authorization", "Basic " + encodeCredentials(USER, PASSWORD)))
                .andDo(print())
                .andExpect(status().isTooManyRequests());
    }

    @DynamicPropertySource
    static void testProperties(DynamicPropertyRegistry registry) {
        registry.add("rate-limiter.max-requests", () -> 1);
        registry.add("rate-limiter.time-window", () -> 1000);
    }
}
