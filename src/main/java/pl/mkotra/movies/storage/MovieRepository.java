package pl.mkotra.movies.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.mkotra.movies.storage.entities.MovieDB;

@Repository
public interface MovieRepository extends JpaRepository<MovieDB, Integer> {

    @Query("SELECT m FROM MovieDB m WHERE m.title LIKE :title")
    Page<MovieDB> findByTitleLike(String title, Pageable pageable);
}
