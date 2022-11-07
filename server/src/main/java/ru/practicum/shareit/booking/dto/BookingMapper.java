package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getStart(),
                booking.getEnd(),
                booking.getItemId()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Long userId) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                userId,
                BookingStatus.WAITING
                );
    }

    public static BookingWithStatusDto toWithStatusBookingDto(Booking booking) {
        return new BookingWithStatusDto(
                booking.getStart(),
                booking.getEnd(),
                booking.getItemId(),
                booking.getStatus()
        );
    }

    public static List<BookingWithStatusDto> toWithStatusList(List<Booking> bookingList) {
        List<BookingWithStatusDto> result = new ArrayList<>();
        for (Booking booking: bookingList) {
            result.add(toWithStatusBookingDto(booking));
        }
        return result;
    }

    public static BookingPostDto toPostDto(Booking booking, Item item, User booker) {
        BookingPostDto.ItemInfo itemInfo = new BookingPostDto.ItemInfo(item.getId(), item.getName());
        BookingPostDto.UserInfo userInfo = new BookingPostDto.UserInfo(booker.getId());
        return new BookingPostDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                userInfo,
                itemInfo,
                booking.getStatus()
        );
    }
}
