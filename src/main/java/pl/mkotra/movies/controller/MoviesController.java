package pl.mkotra.movies.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.mkotra.movies.api.MoviesApi;
import pl.mkotra.movies.core.MovieService;
import pl.mkotra.movies.model.Movie;

import java.util.List;

@RestController
class MoviesController implements MoviesApi {

    private final MovieService moviesService;
    private final PageHeadersFactory pageHeadersFactory;

    MoviesController(MovieService moviesService, PageHeadersFactory pageHeadersFactory) {
        this.moviesService = moviesService;
        this.pageHeadersFactory = pageHeadersFactory;
    }

    @Override
    @Timed(value = "movies.get", description = "Time taken to get movies")
    public ResponseEntity<List<Movie>> moviesGet(Integer page, Integer pageSize, @RequestParam(defaultValue = "%") String name) {
        Page<Movie> actorPage = moviesService.getMovies(name, page, pageSize);

        return ResponseEntity.ok()
                .headers(pageHeadersFactory.create(actorPage))
                .body(actorPage.getContent());
    }

    @Override
    @Timed(value = "movies.id.get", description = "Time taken to get movie by id")
    public ResponseEntity<Movie> moviesIdGet(Integer id) {
        return moviesService.getMovie(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
