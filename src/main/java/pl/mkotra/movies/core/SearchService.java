package pl.mkotra.movies.core;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkotra.movies.model.Movie;
import pl.mkotra.movies.storage.entities.MovieDB;

import java.util.List;

@Service
public class SearchService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public void initialize() {
        SearchSession searchSession = Search.session(entityManager);
        try {
            searchSession.massIndexer().start();
        } catch (Exception e) {
            throw new RuntimeException("Indexing failed", e);
        }
    }

    @Transactional(readOnly = true)
    public void initializeAndWait() {
        SearchSession searchSession = Search.session(entityManager);
        try {
            searchSession.massIndexer().startAndWait();
        } catch (Exception e) {
            throw new RuntimeException("Indexing failed", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Movie> searchMovies(String query, int pageNumber, int pageSize) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<MovieDB> result = searchSession.search(MovieDB.class)
                .where(f -> f.match().field("title").matching(query))
                .sort(f -> f.field("title_sort").asc())
                .fetch(pageNumber, pageSize);

        List<Movie> movies = result.hits().stream()
                .map(MovieMapper.INSTANCE::map)
                .toList();
        long totalHits = result.total().hitCount();

        return new PageImpl<>(movies, PageRequest.of(pageNumber, pageSize), totalHits);
    }
}
