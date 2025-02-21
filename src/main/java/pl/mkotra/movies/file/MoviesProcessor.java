package pl.mkotra.movies.file;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MoviesProcessor extends FileProcessor {

    private static final String SQL = "INSERT INTO movies (id, title, year) VALUES (?, ?, ?);";

    MoviesProcessor(JdbcTemplate jdbcTemplate) {
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
                parseString(nextLine[2], 255),
                parseString(nextLine[5], 4),
        };
    }
}
