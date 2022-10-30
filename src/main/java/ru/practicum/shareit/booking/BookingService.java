package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;

import java.util.List;

public interface BookingService {

    BookingPostDto post(BookingDto bookingDto, Long userId);

    BookingPostDto approve(Long userId, Long bookingId, Boolean approved);

    Booking getById(Long bookingId);

    BookingPostDto getDtoById(Long bookingId, Long userId);

    List<BookingPostDto> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingPostDto> getItemsBookings(Long userId, String state, Integer from, Integer size);
}
