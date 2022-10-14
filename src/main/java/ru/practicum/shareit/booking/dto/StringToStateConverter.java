package ru.practicum.shareit.booking.dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToStateConverter implements Converter<String, State> {
    @Override
    public State convert(String source) {
        String state = source.toUpperCase();
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
    }
}