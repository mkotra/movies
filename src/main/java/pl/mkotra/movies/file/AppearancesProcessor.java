package pl.mkotra.movies.file;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppearancesProcessor extends FileProcessor {

    private static final String DROP_INDEX = "ALTER TABLE appearances DROP INDEX IF EXISTS idx_appearances_actor_id;";
    private static final String SQL = "INSERT INTO appearances(movie_id, actor_id, character_name ) VALUES(?, ?, ?);";
    private static final String RECREATE_INDEX = "CREATE INDEX idx_appearances_actor_id ON appearances(actor_id);";

    AppearancesProcessor(JdbcTemplate jdbcTemplate) {
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
                parseInt(nextLine[0], "tt"),
                parseInt(nextLine[2], "nm"),
                parseString(nextLine[3], 255)
        };
    }
}
