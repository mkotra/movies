package pl.mkotra.movies.controller;

import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.mkotra.movies.file.ActorsProcessor;
import pl.mkotra.movies.file.AppearancesProcessor;
import pl.mkotra.movies.file.MoviesFileProcessor;

import java.io.IOException;

@Tag(name = "populate-data", description = "the populate-data API")
@RestController("/populate-data")
class PopulateDataController {

    private final JdbcTemplate jdbcTemplate;
    private final MoviesFileProcessor moviesFileProcessor;
    private final ActorsProcessor actorsProcessor;
    private final AppearancesProcessor appearancesProcessor;
    private final String imdbFilesBasePath;

    public PopulateDataController(JdbcTemplate jdbcTemplate,
                                  MoviesFileProcessor moviesFileProcessor,
                                  ActorsProcessor actorsProcessor,
                                  AppearancesProcessor appearancesProcessor,
                                  @Value("${imdb-files.base-path}") String imdbFilesBasePath) {
        this.jdbcTemplate = jdbcTemplate;
        this.moviesFileProcessor = moviesFileProcessor;
        this.actorsProcessor = actorsProcessor;
        this.appearancesProcessor = appearancesProcessor;
        this.imdbFilesBasePath = imdbFilesBasePath;
    }

    @PostMapping("/populate")
    void populateData(@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit) throws CsvValidationException, IOException {
        jdbcTemplate.execute("DELETE FROM appearances;");
        jdbcTemplate.execute("DELETE FROM actors;");
        jdbcTemplate.execute("DELETE FROM movies;");

        moviesFileProcessor.process(imdbFilesBasePath + "/title.basics.tsv.gz", limit);
        actorsProcessor.process(imdbFilesBasePath + "/name.basics.tsv.gz", limit);
        appearancesProcessor.process(imdbFilesBasePath + "/title.principals.tsv.gz", limit);
    }
}


