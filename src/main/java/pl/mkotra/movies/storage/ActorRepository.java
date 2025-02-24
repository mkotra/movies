package pl.mkotra.movies.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.ActorDB;

import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<ActorDB, Integer> {

    @Query(value = "SELECT * FROM actors ORDER BY name LIMIT :size OFFSET :offset ", nativeQuery = true)
    List<ActorDB> findWithoutCount(int size, int offset);

    Page<ActorDB> findByNameLike(String name, Pageable pageable);
}