package ru.practicum.shareit.booking.dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        String state = source.toUpperCase();
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
    }
}
