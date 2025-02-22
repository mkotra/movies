package pl.mkotra.movies.core;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.mkotra.movies.model.Actor;
import pl.mkotra.movies.model.Appearance;
import pl.mkotra.movies.storage.ActorRepository;
import pl.mkotra.movies.storage.AppearanceRepository;
import pl.mkotra.movies.storage.entities.ActorDB;
import pl.mkotra.movies.storage.entities.AppearanceDB;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ActorServiceTest {

    ActorRepository actorRepository = mock(ActorRepository.class);
    AppearanceRepository appearanceRepository = mock(AppearanceRepository.class);
    ActorService actorService = new ActorService(actorRepository, appearanceRepository);

    @Test
    void getActorReturnsValidResult() {
        //given
        int actorId = 10;
        String name = "name";
        ActorDB actorDB = new ActorDB();
        actorDB.setId(actorId);
        actorDB.setName(name);
        when(actorRepository.findById(actorId)).thenReturn(Optional.of(actorDB));

        //when
        Optional<Actor> result = actorService.getActor(actorId);

        // then
        assertThat(result)
                .isPresent()
                .get()
                .satisfies(actor -> {
                    assertThat(actor.getId()).isEqualTo(actorId);
                    assertThat(actor.getName()).isEqualTo(name);
                });
    }

    @Test
    void getActorsSReturnsValidResult() {
        //given
        String name = "name";
        int pageNumber = 1;
        int pageSize = 10;
        @SuppressWarnings("unchecked")
        Page<ActorDB> mockResult = mock(Page.class);
        Pageable mockPageable = mock(Pageable.class);
        when(mockResult.getPageable()).thenReturn(mockPageable);
        when(actorRepository.findByNameLike(eq(name), any(Pageable.class))).thenReturn(mockResult);

        //when
        Page<Actor> result = actorService.getActors("name", pageNumber, pageSize);

        // then
        assertThat(result)
                .satisfies(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                    assertThat(page.getPageable()).isEqualTo(mockPageable);
                });

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(actorRepository).findByNameLike(eq("name"), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable).isNotNull();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(capturedPageable.getPageSize()).isEqualTo(pageSize);
        assertThat(capturedPageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Test
    void getAppearancesReturnsValidResult() {
        //given
        int actorId = 5;
        int pageNumber = 1;
        int pageSize = 10;
        @SuppressWarnings("unchecked")
        Page<AppearanceDB> mockResult = mock(Page.class);
        Pageable mockPageable = mock(Pageable.class);
        when(mockResult.getPageable()).thenReturn(mockPageable);
        ActorDB actorDB = new ActorDB();
        actorDB.setId(actorId);
        when(actorRepository.findById(actorId)).thenReturn(Optional.of(actorDB));
        when(appearanceRepository.findAppearanceByActor(eq(actorDB), any(Pageable.class))).thenReturn(mockResult);

        //when
        Page<Appearance> result = actorService.getAppearances(actorId, pageNumber, pageSize);

        // then
        assertThat(result)
                .satisfies(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                    assertThat(page.getPageable()).isEqualTo(mockPageable);
                });

        verify(actorRepository).findById(actorId);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(appearanceRepository).findAppearanceByActor(eq(actorDB), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable).isNotNull();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(capturedPageable.getPageSize()).isEqualTo(pageSize);
        assertThat(capturedPageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "movie.title"));
    }

    @Test
    void getAppearancesWhenActorDoesNotExistReturnsValidResult() {
        //given
        int actorId = 5;
        int pageSize = 10;
        when(actorRepository.findById(actorId)).thenReturn(Optional.empty());

        //when
        Page<Appearance> result = actorService.getAppearances(actorId, 1, pageSize);

        //then
        verify(actorRepository).findById(actorId);
        verifyNoInteractions(appearanceRepository);

        // then
        assertThat(result)
                .satisfies(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(0);
                    assertThat(page.getTotalPages()).isEqualTo(0);
                    assertThat(page.getContent()).isEqualTo(Collections.emptyList());
                    assertThat(page.getPageable()).isEqualTo(Pageable.ofSize(pageSize));
                });
    }
}