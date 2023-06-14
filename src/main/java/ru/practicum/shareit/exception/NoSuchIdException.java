package ru.practicum.shareit.exception;

public class NoSuchIdException extends RuntimeException {
    public NoSuchIdException(String message) {
        super(message);
    }
}
