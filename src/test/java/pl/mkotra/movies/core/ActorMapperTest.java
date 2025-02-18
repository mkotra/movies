package pl.mkotra.movies.core;

import org.junit.jupiter.api.Test;
import pl.mkotra.movies.model.Actor;
import pl.mkotra.movies.storage.entities.ActorDB;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ActorMapperTest {

    @Test
    void shouldMapFields() {
        //given
        ActorDB actorDB = new ActorDB();
        actorDB.setId(1);
        actorDB.setName("name");
        actorDB.setAppearances(Collections.emptySet());

        //when
        Actor result = ActorMapper.INSTANCE.map(actorDB);

        //then
        assertThat(result).satisfies(r -> {
            assertThat(r.getId()).isEqualTo(1);
            assertThat(r.getName()).isEqualTo("name");
        });
    }
}