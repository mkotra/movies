package pl.mkotra.movies.file;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import pl.mkotra.movies.core.CacheService;

@Component
public final class MoviesProcessor extends FileProcessor {

    private static final String DROP_INDEX = "ALTER TABLE movies DROP INDEX IF EXISTS idx_movies_title;";
    private static final String SQL = "INSERT INTO movies (id, title, year) VALUES (?, ?, ?);";
    private static final String RECREATE_INDEX = "CREATE INDEX idx_movies_title ON movies(title);";

    MoviesProcessor(JdbcTemplate jdbcTemplate, CacheService cacheService, FileProcessorProperties fileProcessorProperties) {
        super(jdbcTemplate, cacheService, fileProcessorProperties);
    }

    @Override
    protected void preProcess() {
        jdbcTemplate.execute(DROP_INDEX);
    }

    @Override
    protected String getSql() {
        return SQL;
    }

    @Override
    protected void postProcess() {
        jdbcTemplate.execute(DROP_INDEX);
        jdbcTemplate.execute(RECREATE_INDEX);
        cacheService.invalidate(CacheService.CacheKey.MOVIES_COUNT);
    }

    @Override
    protected Object[] prepareBatchData(String[] nextLine) {
        return new Object[]{
                parseInt(nextLine[0], "tt"),
                parseString(nextLine[2], 255),
                parseString(nextLine[5], 4),
        };
    }
}
