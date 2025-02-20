package pl.mkotra.movies.file;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppearancesProcessor extends FileProcessor {

    private static final String SQL = "INSERT INTO appearances(movie_id, actor_id, character_name ) VALUES(?, ?, ?);";

    AppearancesProcessor(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getSql() {
        return SQL;
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
