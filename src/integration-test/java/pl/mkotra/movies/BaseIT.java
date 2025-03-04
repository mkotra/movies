package pl.mkotra.movies;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = "spring.profiles.include=tests")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public abstract class BaseIT {

    @Container
    static final MariaDBContainer<?> MARIADB = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

    static {
        MARIADB.start();
    }

    protected static final String USER = "user1";
    protected static final String PASSWORD = "pass1";

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected MockMvc mockMvc;

    protected void cleanupTables() {
        jdbcTemplate.execute("TRUNCATE TABLE appearances;");
        jdbcTemplate.execute("TRUNCATE TABLE actors;");
        jdbcTemplate.execute("TRUNCATE TABLE movies;");
    }

    @DynamicPropertySource
    public static void testProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MARIADB::getJdbcUrl);
        registry.add("spring.datasource.username", MARIADB::getUsername);
        registry.add("spring.datasource.password", MARIADB::getPassword);
        registry.add("spring.jpa.properties.hibernate.search.backend.directory.type", () -> "local-heap");
        registry.add("imdb-files.base-path", () -> System.getProperty("user.dir") + "/src/integration-test/resources/");
        registry.add("rate-limiter.max-requests", () -> 1000);
        registry.add("rate-limiter.time-window", () -> 1000);
    }
}
