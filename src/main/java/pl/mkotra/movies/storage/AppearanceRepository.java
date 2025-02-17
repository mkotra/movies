package pl.mkotra.movies.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.ActorDB;
import pl.mkotra.movies.storage.entities.AppearanceDB;

@Repository
public interface AppearanceRepository extends JpaRepository<AppearanceDB, Integer> {
    Page<AppearanceDB> findAppearanceByActor(ActorDB actorDB, Pageable pageable);
}