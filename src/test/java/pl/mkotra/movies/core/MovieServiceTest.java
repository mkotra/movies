package pl.mkotra.movies.core;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.mkotra.movies.model.Movie;
import pl.mkotra.movies.storage.MovieRepository;
import pl.mkotra.movies.storage.entities.MovieDB;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static pl.mkotra.movies.core.CacheService.CacheKey.MOVIES_COUNT;

class MovieServiceTest {

    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final CacheService cachingService = mock(CacheService.class);
    private final MovieService movieService = new MovieService(movieRepository, cachingService);

    @Test
    void getMovieReturnsValidResult() {
        //given
        int movieId = 10;
        String searchValue = "searchValue";
        String year = "year";
        MovieDB movieDB = new MovieDB();
        movieDB.setId(movieId);
        movieDB.setTitle(searchValue);
        movieDB.setYear(year);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movieDB));

        //when
        Optional<Movie> result = movieService.getMovie(movieId);

        // then
        assertThat(result)
                .isPresent()
                .get()
                .satisfies(movie -> {
                    assertThat(movie.getId()).isEqualTo(movieId);
                    assertThat(movie.getTitle()).isEqualTo(searchValue);
                    assertThat(movie.getYear()).isEqualTo(year);
                });
    }

    @Test
    void getMoviesReturnsValidResult() {
        //given
        String searchValue = "searchValue";
        int pageNumber = 1;
        int pageSize = 10;
        @SuppressWarnings("unchecked")
        Page<MovieDB> mockResult = mock(Page.class);
        Pageable mockPageable = mock(Pageable.class);
        when(mockResult.getPageable()).thenReturn(mockPageable);
        when(movieRepository.findByTitleLike(eq(searchValue), any(Pageable.class))).thenReturn(mockResult);

        //when
        Page<Movie> result = movieService.getMovies(searchValue, pageNumber, pageSize);

        // then
        assertThat(result)
                .satisfies(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                    assertThat(page.getPageable()).isEqualTo(mockPageable);
                });

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(movieRepository).findByTitleLike(eq(searchValue), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable).isNotNull();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(capturedPageable.getPageSize()).isEqualTo(pageSize);
        assertThat(capturedPageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "title"));
    }

    @Test
    void getMoviesReturnsWithWildcardAtTheBeginningValidResult() {
        //given
        String searchValue = "%searchValue";
        int pageNumber = 1;
        int pageSize = 10;
        @SuppressWarnings("unchecked")
        Page<MovieDB> mockResult = mock(Page.class);
        Pageable mockPageable = mock(Pageable.class);
        when(mockResult.getPageable()).thenReturn(mockPageable);
        when(movieRepository.findByTitleReversedLike(anyString(), any(Pageable.class))).thenReturn(mockResult);

        //when
        Page<Movie> result = movieService.getMovies(searchValue, pageNumber, pageSize);

        // then
        assertThat(result)
                .satisfies(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                    assertThat(page.getPageable()).isEqualTo(mockPageable);
                });

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(movieRepository).findByTitleReversedLike(eq( "eulaVhcraes%"), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable).isNotNull();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(capturedPageable.getPageSize()).isEqualTo(pageSize);
        assertThat(capturedPageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "title"));
    }

    @Test
    void getMoviesWithOnlyWildcardReturnsValidResult() {
        //given
        int pageNumber = 0;
        int pageSize = 10;
        @SuppressWarnings("unchecked")
        List<MovieDB> mockResult = List.of(mock(MovieDB.class));
        when(movieRepository.findWithoutCount(pageSize, pageNumber)).thenReturn(mockResult);
        when(cachingService.get(CacheService.CacheKey.MOVIES_COUNT)).thenReturn(1L);

        //when
        Page<Movie> result = movieService.getMovies("%", pageNumber, pageSize);

        // then
        assertThat(result)
                .satisfies(page -> {
                    assertThat(page.getContent().size()).isEqualTo(1);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                    assertThat(page.getTotalElements()).isEqualTo(1);
                    assertThat(page.getPageable().getPageNumber()).isEqualTo(pageNumber);
                    assertThat(page.getPageable().getPageSize()).isEqualTo(pageSize);
                    assertThat(page.getPageable().getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "title"));
                });

        verify(movieRepository).findWithoutCount(pageSize, pageNumber);
        verify(cachingService).get(MOVIES_COUNT);
    }
}