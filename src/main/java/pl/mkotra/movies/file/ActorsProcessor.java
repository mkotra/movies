package pl.mkotra.movies.file;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActorsProcessor extends FileProcessor {

    private static final String DROP_INDEX = "ALTER TABLE actors DROP INDEX IF EXISTS idx_actors_name;";
    private static final String SQL = "INSERT INTO actors (id, name) VALUES (?, ?);";
    private static final String RECREATE_INDEX = "CREATE INDEX idx_actors_name ON actors(name);";

    ActorsProcessor(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
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
    }

    @Override
    protected Object[] prepareBatchData(String[] nextLine) {
        return new Object[]{
                parseInt(nextLine[0], "nm"),
                parseString(nextLine[1], 255)
        };
    }
}
