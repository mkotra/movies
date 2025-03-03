package pl.mkotra.movies.controller;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.mkotra.movies.api.MoviesApi;
import pl.mkotra.movies.core.MovieService;
import pl.mkotra.movies.core.SearchService;
import pl.mkotra.movies.model.Movie;

import java.util.List;

@RestController
class MoviesController implements MoviesApi {

    private final MovieService moviesService;
    private final SearchService searchService;
    private final PageHeadersFactory pageHeadersFactory;

    MoviesController(MovieService moviesService, SearchService searchService, PageHeadersFactory pageHeadersFactory) {
        this.moviesService = moviesService;
        this.searchService = searchService;
        this.pageHeadersFactory = pageHeadersFactory;
    }

    @Override
    @Timed(value = "movies.get", description = "Time taken to get movies")
    public ResponseEntity<List<Movie>> moviesGet(Integer page, Integer pageSize, @RequestParam(defaultValue = "%") String name) {
        Page<Movie> moviePage = moviesService.getMovies(name, page, pageSize);

        return ResponseEntity.ok()
                .headers(pageHeadersFactory.create(moviePage))
                .body(moviePage.getContent());
    }

    @Override
    @Timed(value = "movies.id.get", description = "Time taken to get movie by id")
    public ResponseEntity<Movie> moviesIdGet(Integer id) {
        return moviesService.getMovie(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Timed(value = "movies.search", description = "Time taken to search movies")
    @GetMapping("/movies/search")
    public ResponseEntity<List<Movie>> moviesSearch(@Min(0) @RequestParam(defaultValue = "0") Integer page, @Min(1) @Max(1000) @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "*") String name) {
        Page<Movie> moviePage = searchService.searchMovies(name, page, pageSize);

        return ResponseEntity.ok()
                .headers(pageHeadersFactory.create(moviePage))
                .body(moviePage.getContent());
    }
}
