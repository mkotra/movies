package pl.mkotra.movies.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.MovieDB;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieDB, Integer> {

    @Query(value = "SELECT * FROM movies order by title LIMIT :size OFFSET :offset", nativeQuery = true)
    List<MovieDB> findWithoutCount(int size, int offset);

    Page<MovieDB> findByTitleLike(String title, Pageable pageable);
}
