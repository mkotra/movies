package pl.mkotra.movies.storage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.MovieDB;

@Repository
public interface MovieRepository extends JpaRepository<MovieDB, Integer> {

    default Page<MovieDB> findByTitle(String title, Pageable pageable) {
        if (StringUtils.isBlank(title)) {
            return findAll(pageable);
        } else {
            return findByTitleContaining(title, pageable);
        }
    }

    Page<MovieDB> findByTitleContaining(String title, Pageable pageable);
}
