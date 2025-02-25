package pl.mkotra.movies.file;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import pl.mkotra.movies.core.CacheService;

@Component
public final class MoviesProcessor extends FileProcessor {

    private static final String DROP_INDEX = "ALTER TABLE movies DROP INDEX IF EXISTS idx_movies_title;";
    private static final String DROP_REVERSED_INDEX = "ALTER TABLE movies DROP INDEX IF EXISTS idx_movies_title_reversed;";
    private static final String SQL = "INSERT INTO movies (id, title, title_reversed, year) VALUES (?, ?, ?, ?);";
    private static final String RECREATE_INDEX = "CREATE INDEX idx_movies_title ON movies(title);";
    private static final String RECREATE_REVERSED_INDEX = "CREATE INDEX idx_movies_title_reversed ON movies(title_reversed);";


    MoviesProcessor(JdbcTemplate jdbcTemplate, CacheService cacheService, FileProcessorProperties fileProcessorProperties) {
        super(jdbcTemplate, cacheService, fileProcessorProperties);
    }

    @Override
    protected void preProcess() {
        jdbcTemplate.execute(DROP_INDEX);
        jdbcTemplate.execute(DROP_REVERSED_INDEX);
    }

    @Override
    protected String getSql() {
        return SQL;
    }

    @Override
    protected void postProcess() {
        jdbcTemplate.execute(DROP_INDEX);
        jdbcTemplate.execute(DROP_REVERSED_INDEX);
        jdbcTemplate.execute(RECREATE_INDEX);
        jdbcTemplate.execute(RECREATE_REVERSED_INDEX);
        cacheService.invalidate(CacheService.CacheKey.MOVIES_COUNT);
    }

    @Override
    protected Object[] prepareBatchData(String[] nextLine) {
        String title = parseString(nextLine[2], 255);
        return new Object[]{
                parseInt(nextLine[0], "tt"),
                title,
                StringUtils.reverse(title),
                parseString(nextLine[5], 4),

        };
    }
}
