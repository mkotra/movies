# Movies Spring Boot Demo

This is a Spring Boot application that exposes REST API according to requirements provided in
api-spec.yaml

## Overall Goal
The goal was to provide something maybe not 100% functional, but with decent quality and code coverage, to allow further
improvements.

## Implemented Features
- **Database storage** to keep data in tables: actors, movies, appearances
- **Loading data from imdb files**
- **OpenAPI Swagger UI web client**
- **Basic auth** security with three users hardcoded in application.yaml
- **Paging info included in custom response headers**
- **Rate limiter** to protect the API
- **Wildcard search** according to the SQL syntax: https://www.w3schools.com/sql/sql_wildcards.asp
- **Prometheus metrics**

## Prerequisites

To run the application, we will need:

- **Java 23**
- **Docker** (required for integration tests and can also set up MariaDB and Prometheus UI)
- **MariaDB**

## Running the Application

### Setting Up Infrastructure

Use Docker and `docker-compose.yml` to set up the necessary infrastructure:

```sh
docker compose up
```

### Building the Application

This will build the application and also run both unit and integration tests:

```sh
mvn verify
```

### Starting the Application

Start the Spring Boot application using Maven:

```sh
mvn spring-boot:run
```

### API Documentation

REST API can be tested using Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

### Metrics & Monitoring

Actuator metrics can be accessed here:

```text
http://localhost:8080/actuator/metrics
```

Actuator metrics for Prometheus can be accessed here:

```text
http://localhost:8080/actuator/prometheus
```

Prometheus UI can be accessed here:

```text
http://localhost:9090
```

Useful metrics:

```scss
movies_get_seconds_sum

/
movies_get_seconds_count
```

```scss
max

(
movies_get_seconds_max

)
```

### Exploring API

- Please use **SwaggerUI**
- **Authorize** - please check application.yaml security section to obtain credentials.
- Populate data into database **populate data API**, please make sure imdb files are stored in proper location.
- Start using actors API and movies API

### WebUI

Simple Angular based Web UI was implemented to display movie list.
Node.js LTS needs to be installed in the system to build it and run.

Please also install Angular cli (if not installed):

```sh
npm install -g @angular/cli
```

Then please navigate to the `ui/` directory and run:

```sh
ng serve
```

Simple Web UI can be accessed here:

```text
http://localhost:4200
```

Web UI consumes movies API, please remember about RPS limit that may result with 429 errors.

## Further improvements proposal
- Drop Primary Key – This may speed up data insertion but could introduce duplicates.
- Load Files Directly into the Database – Using `LOAD DATA INFILE` can accelerate the process, however it may lead to
  data consistency issues.
- Scalability Considerations – For very large datasets proposed solution might be insufficient in terms of
  performance.

## Indexing problem
The following query will not use an index due to the way BTREE indexes function:

```sql
    EXPLAIN SELECT *
    FROM movies
    WHERE title LIKE '%Movie%'; 
```

Since the search pattern includes a leading wildcard (`%`), MariaDB cannot utilize the BTREE index efficiently and must
perform a full table scan instead.

Indexes can also consume a significant amount of disk space, especially as the dataset grows:

```sql
SELECT table_name,
       ROUND(data_length / 1024 / 1024, 2)                  AS data_size_mb,
       ROUND(index_length / 1024 / 1024, 2)                 AS index_size_mb,
       ROUND((data_length + index_length) / 1024 / 1024, 2) AS total_size_mb
FROM information_schema.tables
WHERE table_schema = 'movies_db'
  AND table_name = 'movies';
```

A possible solution to improve query performance is to use **Hibernate Search** backed by Apache Lucene.

[![Java CI with Maven](https://github.com/mkotra/movies/actions/workflows/maven.yml/badge.svg)](https://github.com/mkotra/spring/actions/workflows/maven.yml)