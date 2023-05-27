package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
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
}
