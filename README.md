# Movies Spring Boot Demo

This is a Spring Boot application that exposes REST API according to requirements provided in
api-spec.yaml

## Overall Goal
The goal was to provide something maybe not 100% functional, but with decent quality and code coverage, to allow further
improvements.

## Implemented Features
- **Database storage** to keep data in tables: actors, movies, appearances **
- **OpenAPI Swagger UI web client**
- **Basic auth** security with three users hardcoded in application.yaml
- **Paging info included in custom response headers**
- **Rate limiter** to protect the API
- **Unit and Integration tests**
- **Prometheus metrics**

## Missing Features
Due to limited time for the task I did not manage to implement:
- **Load the data from tsv files** - only some random data for now.
- **WebUI** - I was planning to provide maybe some simple Angular based Web UI, but only managed to provide SwaggerUI.
- **Custom Micrometer Metrics** - there are only defaults provided by Spring.

## Prerequisites
To run the application, we will need:
- **Java 23**
- **Docker** (required for integration tests and can also set up MariaDB)
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

### Exploring API
- Please use **SwaggerUI**
- **Authorize** - please check application.yaml security section to obtain credentials.
- Populate some random data into database **populate data API**
- Start using actors API and movies API

[![Java CI with Maven](https://github.com/mkotra/movies/actions/workflows/maven.yml/badge.svg)](https://github.com/mkotra/spring/actions/workflows/maven.yml)