package pl.mkotra.movies.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.mkotra.movies.BaseIT;
import pl.mkotra.movies.core.CacheService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MoviesControllerIT extends BaseIT {

    @Autowired
    private CacheService cacheService;

    @BeforeEach
    void setupDatabase() {
        cacheService.invalidate(CacheService.CacheKey.ACTORS_COUNT);
        cacheService.invalidate(CacheService.CacheKey.MOVIES_COUNT);
        cleanupTables();

        jdbcTemplate.execute("INSERT INTO movies (id, title, title_reversed, year) VALUES (1, 'The Godfather', REVERSE('The Godfather'), '1972');");
        jdbcTemplate.execute("INSERT INTO movies (id, title, title_reversed, year) VALUES (2, 'The Shawshank Redemption', REVERSE('The Shawshank Redemption'), '1994');");
    }

    @Test
    void moviesGetReturnsValidResponse() throws Exception {

        mockMvc.perform(get("/movies")
                        .with(httpBasic(USER, PASSWORD))
                        .param("name", "%God_ather%")
                        .param("page", "0")
                        .param("page_size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Size", "1"))
                .andExpect(header().string("X-Total-Pages", "1"))
                .andExpect(header().string("X-Page-Number", "0"))
                .andExpect(header().string("X-Page-Size", "10"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(1)))
                .andExpect(jsonPath("$[0].title").value(is("The Godfather")))
                .andExpect(jsonPath("$[0].year").value(is("1972")));
    }

    @Test
    void moviesGetWithMissingNameReturnsValidResponse() throws Exception {
        mockMvc.perform(get("/movies")
                        .with(httpBasic(USER, PASSWORD))
                        .param("page", "0")
                        .param("page_size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Size", "2"));
    }

    @Test
    void moviesGetWithInvalidPageSizeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/movies")
                        .with(httpBasic(USER, PASSWORD))
                        .param("page", "0")
                        .param("page_size", "1001"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void moviesGetReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/movies"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void moviesIdGetReturnsValidResponse() throws Exception {

        mockMvc.perform(get("/movies/{id}", 1)
                        .with(httpBasic(USER, PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(1)))
                .andExpect(jsonPath("$.title").value(is("The Godfather")))
                .andExpect(jsonPath("$.year").value(is("1972")));
    }

    @Test
    void moviesIdGetReturnsNotFound() throws Exception {
        mockMvc.perform(get("/movies/{id}", 12345)
                        .with(httpBasic(USER, PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void moviesIdGetReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/movies/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void moviesSearchReturnsValidResponse() throws Exception {

        mockMvc.perform(get("/movies")
                        .with(httpBasic(USER, PASSWORD))
                        .param("name", "*Godfather*")
                        .param("page", "0")
                        .param("page_size", "10"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
