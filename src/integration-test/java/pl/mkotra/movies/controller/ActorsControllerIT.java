package pl.mkotra.movies.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.mkotra.movies.BaseIT;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ActorsControllerIT extends BaseIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setupDatabase() {
        jdbcTemplate.execute("INSERT INTO movies (id, title, year) VALUES (1, 'Inception', '2010');");
        jdbcTemplate.execute("INSERT INTO actors (id, name) VALUES (2, 'Leonardo DiCaprio');");
        jdbcTemplate.execute("INSERT INTO appearances(movie_id, actor_id, appearance_character ) VALUES(1, 2, 'Cobb');");
    }

    @Test
    void actorsGetReturnsValidResponse() throws Exception {

        mockMvc.perform(get("/actors")
                        .param("name", "Leonardo DiCaprio")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Size", "1"))
                .andExpect(header().string("X-Total-Pages", "1"))
                .andExpect(header().string("X-Page-Number", "0"))
                .andExpect(header().string("X-Page-Size", "10"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(is(2)))
                .andExpect(jsonPath("$[0].name").value(is("Leonardo DiCaprio")));
    }

    @Test
    void actorsIdAppearancesGetReturnsValidResponse() throws Exception {
        mockMvc.perform(get("/actors/{id}/appearances", 2)
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Size", "1"))
                .andExpect(header().string("X-Total-Pages", "1"))
                .andExpect(header().string("X-Page-Number", "0"))
                .andExpect(header().string("X-Page-Size", "10"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].movie_id").value(is(1)))
                .andExpect(jsonPath("$[0].movie_name").value(is("Inception")))
                .andExpect(jsonPath("$[0].character_name").value(is("Cobb")));
    }

    @Test
    void actorsIdGetReturnsValidResponse() throws Exception {

        mockMvc.perform(get("/actors/{id}", 2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(2)))
                .andExpect(jsonPath("$.name").value(is("Leonardo DiCaprio")));
    }

    @Test
    void actorsIdGetReturnsNotFound() throws Exception {
        mockMvc.perform(get("/actors/{id}", 12345))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
