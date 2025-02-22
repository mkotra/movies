package pl.mkotra.movies.core;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import pl.mkotra.movies.model.Actor;
import pl.mkotra.movies.model.Appearance;
import pl.mkotra.movies.storage.ActorRepository;
import pl.mkotra.movies.storage.AppearanceRepository;
import pl.mkotra.movies.storage.entities.ActorDB;
import pl.mkotra.movies.storage.entities.AppearanceDB;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ActorService {

    private static final Sort ACTORS_SORT = Sort.by(Sort.Direction.ASC, "name");
    private static final Sort APPEARANCES_SORT = Sort.by(Sort.Direction.ASC, "movie.title");

    private final ActorRepository actorRepository;
    private final AppearanceRepository appearanceRepository;

    ActorService(ActorRepository actorRepository, AppearanceRepository appearanceRepository) {
        this.actorRepository = actorRepository;
        this.appearanceRepository = appearanceRepository;
    }

    public Optional<Actor> getActor(int id) {
        return actorRepository.findById(id).map(ActorMapper.INSTANCE::map);
    }

    public Page<Actor> getActors(String name, Integer pageNumber, Integer pageSize) {

        Page<ActorDB> actorDBPage = actorRepository.findByNameLike(name, PageRequest.of(pageNumber, pageSize, ACTORS_SORT));

        List<Actor> actors = actorDBPage.getContent().stream()
                .map(ActorMapper.INSTANCE::map)
                .toList();

        return new PageImpl<>(actors, actorDBPage.getPageable(), actorDBPage.getTotalElements());
    }

    public Page<Appearance> getAppearances(Integer actorId, Integer pageNumber, Integer pageSize) {
        return actorRepository.findById(actorId)
                .map(actorDB -> {
                    PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, APPEARANCES_SORT);
                    Page<AppearanceDB> appearancesDB = appearanceRepository.findAppearanceByActor(actorDB, pageRequest);
                    List<Appearance> appearances = appearancesDB.getContent().stream()
                            .map(AppearanceMapper.INSTANCE::map)
                            .toList();
                    return new PageImpl<>(appearances, appearancesDB.getPageable(), appearancesDB.getTotalElements());
                })
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), Pageable.ofSize(pageSize), 0));
    }
}
