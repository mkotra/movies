package pl.mkotra.movies.file;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public abstract class FileProcessor {

    private static final int BATCH_SIZE = 1000;

    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    protected final JdbcTemplate jdbcTemplate;

    protected FileProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void process(String filePath, int limit) throws IOException, CsvValidationException {
        try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(filePath));
             BufferedReader br = new BufferedReader(new InputStreamReader(gis));
             CSVReader reader = new CSVReaderBuilder(br)
                     .withCSVParser(new CSVParserBuilder()
                             .withSeparator('\t')
                             //this important as openCSV parser has some issues with multiline rows
                             .withIgnoreQuotations(true)
                             .build())
                     .build()) {

            String[] nextLine;
            reader.readNext();
            int lineNumber = 0;
            List<Object[]> batchData = new ArrayList<>();
            while ((nextLine = reader.readNext()) != null) {
                lineNumber++;
                try {
                    batchData.add(prepareBatchData(nextLine));
                } catch (Exception e) {
                    logger.error("Cannot parse row {} ", lineNumber);
                }

                if (batchData.size() >= BATCH_SIZE) {
                    logger.info("Inserting batch of {}, current line number is: {}", BATCH_SIZE, lineNumber);
                    try {
                        insertBatch(batchData);
                    } catch (Exception e) {
                        logger.error("Error inserting {} - file: {}", e.getMessage(), filePath);
                    }
                    batchData.clear();
                }
                if (lineNumber >= limit) {
                    break;
                }
            }

            if (!batchData.isEmpty()) {
                insertBatch(batchData);
            }

            logger.info("All {} records inserted successfully.", lineNumber);
        }
    }

    protected abstract String getSql();

    protected abstract Object[] prepareBatchData(String[] nextLine);

    protected static int parseInt(String value, String prefix) {
        String intString = value.startsWith(prefix) ? value.substring(2) : value;
        return Integer.parseInt(intString);
    }

    protected static String parseString(String input, int limit) {
        return input.length() > limit ? input.substring(0, limit) : input;
    }

    private void insertBatch(List<Object[]> batchData) {
        jdbcTemplate.batchUpdate(getSql(), batchData);
    }
}
