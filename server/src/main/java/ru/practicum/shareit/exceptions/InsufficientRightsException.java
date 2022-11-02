package ru.practicum.shareit.exceptions;

public class InsufficientRightsException extends RuntimeException {
    public InsufficientRightsException(String message) {
        super(message);
    }
}
