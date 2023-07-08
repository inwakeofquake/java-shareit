package ru.practicum.shareit.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUnsupportedState(final UnsupportedStateException e) {
        log.error("Unsupported state error. Response code: {}", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                Map.of(
                        "error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

}
