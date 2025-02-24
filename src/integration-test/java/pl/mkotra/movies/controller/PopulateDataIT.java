package pl.mkotra.movies.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.mkotra.movies.BaseIT;
import pl.mkotra.movies.core.CacheService;
import pl.mkotra.movies.storage.ActorRepository;
import pl.mkotra.movies.storage.AppearanceRepository;
import pl.mkotra.movies.storage.MovieRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PopulateDataIT extends BaseIT {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private AppearanceRepository appearanceRepository;
    @Autowired
    private CacheService cacheService;

    @Test
    void populateSavesDataFromFilesIntoDB() throws Exception {
        int limit = 10;

        assertThat(movieRepository.count()).isEqualTo(0);
        assertThat(actorRepository.count()).isEqualTo(0);
        assertThat(appearanceRepository.count()).isEqualTo(0);

        mockMvc.perform(post("/populate")
                        .with(httpBasic(USER, PASSWORD))
                        .param("limit", String.valueOf(limit)))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(movieRepository.count()).isEqualTo(limit);
        assertThat(actorRepository.count()).isEqualTo(limit);
        assertThat(appearanceRepository.count()).isEqualTo(limit);

        assertThat(cacheService.get(CacheService.CacheKey.MOVIES_COUNT)).isEqualTo(limit);
        assertThat(cacheService.get(CacheService.CacheKey.ACTORS_COUNT)).isEqualTo(limit);
    }
}
