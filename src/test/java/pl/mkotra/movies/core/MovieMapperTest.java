package pl.mkotra.movies.core;

import org.junit.jupiter.api.Test;
import pl.mkotra.movies.model.Movie;
import pl.mkotra.movies.storage.entities.MovieDB;

import static org.assertj.core.api.Assertions.assertThat;

class MovieMapperTest {

    @Test
    void shouldMapFields() {
        //given
        MovieDB movieDB = new MovieDB();
        movieDB.setId(1);
        movieDB.setTitle("title");
        movieDB.setYear("year");

        //when
        Movie result = MovieMapper.INSTANCE.map(movieDB);

        //then
        assertThat(result).satisfies(r -> {
            assertThat(r.getId()).isEqualTo(1);
            assertThat(r.getTitle()).isEqualTo("title");
            assertThat(r.getYear()).isEqualTo("year");
        });
    }
}