package pl.mkotra.movies.storage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.ActorDB;

@Repository
public interface ActorRepository extends JpaRepository<ActorDB, Integer> {

    default Page<ActorDB> findByName(String name, Pageable pageable) {
        if (StringUtils.isBlank(name)) {
            return findAll(pageable);
        } else {
            return findByNameContaining(name, pageable);
        }
    }

    Page<ActorDB> findByNameContaining(String name, Pageable pageable);
}