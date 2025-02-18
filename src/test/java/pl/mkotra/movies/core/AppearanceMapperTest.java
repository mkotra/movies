package pl.mkotra.movies.core;

import org.junit.jupiter.api.Test;
import pl.mkotra.movies.model.Appearance;
import pl.mkotra.movies.storage.entities.ActorDB;
import pl.mkotra.movies.storage.entities.AppearanceDB;
import pl.mkotra.movies.storage.entities.MovieDB;

import static org.assertj.core.api.Assertions.assertThat;

class AppearanceMapperTest {

    @Test
    void shouldMapFields() {
        //given
        MovieDB movieDB = new MovieDB();
        movieDB.setId(1);
        movieDB.setTitle("title");
        movieDB.setYear("year");

        AppearanceDB appearanceDB = new AppearanceDB();
        appearanceDB.setMovie(movieDB);
        appearanceDB.setActor(new ActorDB());
        appearanceDB.setCharacterName("characterName");

        //when
        Appearance result = AppearanceMapper.INSTANCE.map(appearanceDB);

        //then
        assertThat(result).satisfies(r -> {
            assertThat(r.getMovieId()).isEqualTo(1);
            assertThat(r.getMovieName()).isEqualTo("title");
            assertThat(r.getCharacterName()).isEqualTo("characterName");
        });
    }
}