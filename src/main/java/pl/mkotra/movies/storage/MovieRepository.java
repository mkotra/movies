package pl.mkotra.movies.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.MovieDB;

@Repository
public interface MovieRepository extends JpaRepository<MovieDB, Integer> {
    Page<MovieDB> findByTitleContaining(String title, Pageable pageable);
}
