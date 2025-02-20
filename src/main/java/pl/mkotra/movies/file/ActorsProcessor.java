package pl.mkotra.movies.file;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActorsProcessor extends FileProcessor {

    private static final String SQL = "INSERT INTO actors (id, name) VALUES (?, ?);";

    ActorsProcessor(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getSql() {
        return SQL;
    }

    @Override
    protected Object[] prepareBatchData(String[] nextLine) {
        return new Object[]{
                parseInt(nextLine[0], "nm"),
                parseString(nextLine[1], 255)
        };
    }
}
