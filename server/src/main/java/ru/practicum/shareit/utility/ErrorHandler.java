package ru.practicum.shareit.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNoSuchId(final NoSuchIdException e) {
        log.error("No such ID error occurred. Response code: {}", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                Map.of(
                        "error", "No such ID",
                        "errorMessage", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNoAvailable(final BadRequestException e) {
        log.error("Item not available. Response code: {}", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                Map.of(
                        "error", "Not available",
                        "errorMessage", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUnsupportedState(final UnsupportedStateException e) {
        log.error("Unsupported state error. Response code: {}", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                Map.of(
                        "error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> invalidInput(final InvalidInputException e) {
        log.error("Invalid input error occurred. Response code: {}", HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(
                Map.of(
                        "error", "Invalid input",
                        "errorMessage", e.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    public ResponseEntity<Map<String, String>> exceptionFound(final RuntimeException e) {
        log.error("Exception found. Response code: {}", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(
                Map.of(
                        "error", "Exception found",
                        "errorMessage", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            String path = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            errors.put(path, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
