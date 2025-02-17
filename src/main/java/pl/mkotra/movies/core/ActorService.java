package pl.mkotra.movies.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.mkotra.movies.model.Actor;
import pl.mkotra.movies.model.Appearance;
import pl.mkotra.movies.storage.ActorRepository;
import pl.mkotra.movies.storage.AppearanceRepository;
import pl.mkotra.movies.storage.entities.ActorDB;
import pl.mkotra.movies.storage.entities.AppearanceDB;
import pl.mkotra.movies.storage.entities.MovieDB;

import java.util.List;
import java.util.Optional;

@Service
public class ActorService {

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

        Page<ActorDB> actorDBPage = actorRepository.findByName(name, PageRequest.of(pageNumber, pageSize));

        List<Actor> actors = actorDBPage.getContent().stream()
                .map(ActorMapper.INSTANCE::map)
                .toList();

        return new PageImpl<>(actors, actorDBPage.getPageable(), actorDBPage.getTotalElements());
    }

    public Page<Appearance> getAppearances(Integer actorId, Integer pageNumber, Integer pageSize) {
        //TODO: handle
        ActorDB actorDB = actorRepository.findById(actorId).orElse(null);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "movie.title"));
        Page<AppearanceDB> appearancesDB = appearanceRepository.findAppearanceByActor(actorDB, pageRequest);

        List<Appearance> appearances = appearancesDB.getContent().stream()
                .map(appearanceDB -> {
                    Appearance appearance = new Appearance();
                    MovieDB movieDB = appearanceDB.getMovie();
                    appearance.setMovieId(movieDB.getId());
                    appearance.setMovieName(movieDB.getTitle());
                    appearance.setCharacterName(appearanceDB.getCharacter());
                    return appearance;
                }).toList();

        return new PageImpl<>(appearances, appearancesDB.getPageable(), appearancesDB.getTotalElements());
    }
}
