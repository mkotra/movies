package pl.mkotra.movies.core;

import org.junit.jupiter.api.Test;
import pl.mkotra.movies.storage.ActorRepository;
import pl.mkotra.movies.storage.MovieRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static pl.mkotra.movies.core.CacheService.CacheKey.ACTORS_COUNT;
import static pl.mkotra.movies.core.CacheService.CacheKey.MOVIES_COUNT;

class CacheServiceTest {
    
    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final ActorRepository actorRepository = mock(ActorRepository.class);
    private final CacheService cacheService = new CacheService(movieRepository, actorRepository);
    
    @Test
    void getReturnsValidWhenLoaded() {
        //given
        cacheService.get(ACTORS_COUNT);
        cacheService.get(ACTORS_COUNT);

        //then
        verify(actorRepository, times(1)).count();
    }

    @Test
    void getReturnsValidWhenNotLoaded() {
        //given
        when(actorRepository.count()).thenReturn(100L);
        when(movieRepository.count()).thenReturn(200L);

        //when
        Long actorCount = cacheService.get(ACTORS_COUNT);
        Long movieCount = cacheService.get(MOVIES_COUNT);

        //then
        assertThat(actorCount).isEqualTo(100L);
        assertThat(movieCount).isEqualTo(200L);

        verify(actorRepository, times(1)).count();
        verify(movieRepository, times(1)).count();
    }

    @Test
    void invalidateClearsCacheEntry() {
        //given
        when(actorRepository.count()).thenReturn(150L);
        cacheService.get(ACTORS_COUNT);

        //when
        cacheService.invalidate(ACTORS_COUNT);
        cacheService.get(ACTORS_COUNT);

        //then
        verify(actorRepository, times(2)).count();
    }
}
