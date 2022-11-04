package ru.practicum.shareit.errorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {BookingController.class})
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final Exception e) {
        return Map.of("error", e.getMessage());
    }

}