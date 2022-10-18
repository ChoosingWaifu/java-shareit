package ru.practicum.shareit.errorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.ItemController;
import ru.practicum.shareit.item.model.ItemServiceImpl;
import ru.practicum.shareit.user.UserController;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class, ItemServiceImpl.class})
public class ErrorHandler {

    @ExceptionHandler({NullStatusException.class, UnavailableItemException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final Exception e) {
        return Map.of("validation exception", e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(final Exception e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({NotFoundException.class, InsufficientRightsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoFoundExceptions(final Exception e) {
        return Map.of("not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleServerErrorExceptions(final IOException e) {
        return Map.of("IO exception", e.getMessage());
    }
}

