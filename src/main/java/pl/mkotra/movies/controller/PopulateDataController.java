package pl.mkotra.movies.controller;

import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.mkotra.movies.core.SearchService;
import pl.mkotra.movies.file.ActorsProcessor;
import pl.mkotra.movies.file.AppearancesProcessor;
import pl.mkotra.movies.file.MoviesProcessor;

import java.io.IOException;

@Tag(name = "populate-data", description = "the populate-data API")
@RestController("/populate-data")
class PopulateDataController {

    private static final Logger logger = LoggerFactory.getLogger(PopulateDataController.class);

    private final JdbcTemplate jdbcTemplate;
    private final MoviesProcessor moviesProcessor;
    private final ActorsProcessor actorsProcessor;
    private final AppearancesProcessor appearancesProcessor;
    private final SearchService searchService;

    public PopulateDataController(JdbcTemplate jdbcTemplate,
                                  MoviesProcessor moviesProcessor,
                                  ActorsProcessor actorsProcessor,
                                  AppearancesProcessor appearancesProcessor,
                                  SearchService searchService) {
        this.jdbcTemplate = jdbcTemplate;
        this.moviesProcessor = moviesProcessor;
        this.actorsProcessor = actorsProcessor;
        this.appearancesProcessor = appearancesProcessor;
        this.searchService = searchService;
    }

    @PostMapping("/populate")
    void populateData(@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit) throws CsvValidationException, IOException {
        logger.info("Cleanup old data...");
        jdbcTemplate.execute("TRUNCATE TABLE appearances;");
        jdbcTemplate.execute("TRUNCATE TABLE actors;");
        jdbcTemplate.execute("TRUNCATE TABLE movies;");

        logger.info("Files processing started...");

        moviesProcessor.process("title.basics.tsv.gz", limit);
        logger.info("Movies processing competed!");

        actorsProcessor.process("name.basics.tsv.gz", limit);
        logger.info("Actors processing competed!");

        appearancesProcessor.process("title.principals.tsv.gz", limit);
        logger.info("Appearances processing competed!");

        //initializes Lucene Index rebuild (supports movies only).
        searchService.initialize();
    }
}


