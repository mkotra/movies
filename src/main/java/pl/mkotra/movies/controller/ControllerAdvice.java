package pl.mkotra.movies.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("errors", errors);

        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Not Found");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
        problemDetail.setTitle("Internal Server Error");
        return new ResponseEntity<>(problemDetail, httpStatus);
    }

}
