package pl.mkotra.movies.core;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.mkotra.movies.model.Movie;
import pl.mkotra.movies.storage.MovieRepository;
import pl.mkotra.movies.storage.entities.MovieDB;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    MovieRepository movieRepository = mock(MovieRepository.class);
    MovieService movieService = new MovieService(movieRepository);

    @Test
    void getMovieReturnsValidResult() {
        //given
        int movieId = 10;
        String title = "title";
        String year = "year";
        MovieDB movieDB = new MovieDB();
        movieDB.setId(movieId);
        movieDB.setTitle(title);
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
                    assertThat(movie.getTitle()).isEqualTo(title);
                    assertThat(movie.getYear()).isEqualTo(year);
                });
    }

    @Test
    void getMovieSReturnsValidResult() {
        //given
        String title = "title";
        int pageNumber = 1;
        int pageSize = 10;
        @SuppressWarnings("unchecked")
        Page<MovieDB> mockResult = mock(Page.class);
        Pageable mockPageable = mock(Pageable.class);
        when(mockResult.getPageable()).thenReturn(mockPageable);
        when(movieRepository.findByTitleLike(eq(title), any(Pageable.class))).thenReturn(mockResult);

        //when
        Page<Movie> result = movieService.getMovies("title", pageNumber, pageSize);

        // then
        assertThat(result)
                .satisfies(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                    assertThat(page.getPageable()).isEqualTo(mockPageable);
                });

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(movieRepository).findByTitleLike(eq("title"), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable).isNotNull();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(capturedPageable.getPageSize()).isEqualTo(pageSize);
        assertThat(capturedPageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "title"));
    }
}