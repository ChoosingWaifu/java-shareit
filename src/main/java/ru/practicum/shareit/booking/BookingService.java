package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

public interface BookingService {

    BookingPostDto post(BookingDto bookingDto, Long userId) throws NotFoundException, InsufficientRightsException, UnavailableItemException, ValidationException;

    BookingPostDto approve(Long userId, Long bookingId, Boolean approved) throws NotFoundException, InsufficientRightsException, ValidationException;

    Booking getById(Long bookingId) throws NotFoundException;

    BookingPostDto getDtoById(Long bookingId, Long userId) throws NotFoundException, InsufficientRightsException;

    List<BookingPostDto> getUserBookings(Long userId, String state, Integer from, Integer size) throws NotFoundException;

    List<BookingPostDto> getItemsBookings(Long userId, String state, Integer from, Integer size) throws NotFoundException;
}
