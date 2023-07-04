package ru.practicum.shareit.utility;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}