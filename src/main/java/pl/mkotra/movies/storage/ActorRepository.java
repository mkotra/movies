package pl.mkotra.movies.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.ActorDB;

@Repository
public interface ActorRepository extends JpaRepository<ActorDB, Integer> {

    Page<ActorDB> findByNameContaining(String name, Pageable pageable);
}