package pl.mkotra.movies.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.mkotra.movies.model.Movie;
import pl.mkotra.movies.storage.MovieRepository;
import pl.mkotra.movies.storage.entities.MovieDB;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private static final Sort SORT = Sort.by(Sort.Direction.ASC, "title");

    private final MovieRepository movieRepository;

    MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Optional<Movie> getMovie(Integer id) {
        return movieRepository.findById(id).map(MovieMapper.INSTANCE::map);
    }

    public Page<Movie> getMovies(String title, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, SORT);
        Page<MovieDB> movieDBPage = movieRepository.findByTitleLike(title, pageRequest);

        List<Movie> actors = movieDBPage.getContent().stream()
                .map(MovieMapper.INSTANCE::map)
                .toList();

        return new PageImpl<>(actors, movieDBPage.getPageable(), movieDBPage.getTotalElements());
    }
}
