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

public abstract sealed class FileProcessor permits ActorsProcessor, AppearancesProcessor, MoviesProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    protected final JdbcTemplate jdbcTemplate;
    private final String basePath;
    private final int batchSize;

    FileProcessor(JdbcTemplate jdbcTemplate,
                  FileProcessorProperties fileProcessorProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.basePath = fileProcessorProperties.getBasePath();
        this.batchSize = fileProcessorProperties.getInsertBatchSize();
    }

    public void process(String fileName, int limit) throws IOException, CsvValidationException {
        logger.info("Pre processing started...");
        preProcess();
        logger.info("Pre processing completed!");

        String filePath = basePath + "/" + fileName;
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

                if (batchData.size() >= batchSize) {
                    logger.info("Inserting batch of {}, {} - current line number is: {}", batchSize, fileName, lineNumber);
                    try {
                        insertBatch(batchData);
                    } catch (Exception e) {
                        logger.error("Error inserting {} - file: {}", e.getMessage(), fileName);
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

            logger.info("All {} records from {} inserted successfully.", lineNumber, fileName);
        }

        logger.info("Post processing for {} started...", fileName);
        postProcess();
        logger.info("Post processing completed!");
    }

    protected abstract void preProcess();

    protected abstract void postProcess();

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
