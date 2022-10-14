package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface BookingService {

    Booking post(Booking booking);

    Booking approve(Long userId, Long BookingId, Boolean approved) throws NotFoundException, InsufficientRightsException;

    Booking getById(Long bookingId) throws NotFoundException;

    List<BookingPostDto> getUserBookings(Long userId, State state) throws NotFoundException;

    List<BookingPostDto> getItemsBookings(Long userId, State state) throws NotFoundException;
}
