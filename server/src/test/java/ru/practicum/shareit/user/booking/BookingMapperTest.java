package ru.practicum.shareit.user.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingWithStatusDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BookingMapperTest {

    private final User user1 = new User(1L, "user1", "user1@yandex.ru");

    private final Item item1 = new Item(1L,"item1","desc1",true,1L,null, new ArrayList<>());

    private final Booking booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), item1.getId(), user1.getId(), BookingStatus.WAITING);

    private final BookingDto bookingDto1 = new BookingDto(booking1.getStart(), booking1.getEnd(), item1.getId());

    @Test
    void toBookingTest() {
        Booking result = BookingMapper.toBooking(bookingDto1, booking1.getBooker());
        result.setId(booking1.getId());
        Assertions.assertEquals(result.toString(), booking1.toString());
    }

    @Test
    void toWithStatusTest() {
        BookingWithStatusDto result = BookingMapper.toWithStatusBookingDto(booking1);
        Assertions.assertEquals(result.getStart(), booking1.getStart());
        Assertions.assertEquals(result.getEnd(), booking1.getEnd());
        Assertions.assertEquals(result.getItemId(), booking1.getItemId());
        Assertions.assertEquals(result.getStatus(), booking1.getStatus());
    }

    @Test
    void toPostDtoTest() {
        BookingPostDto result = BookingMapper.toPostDto(booking1, item1, user1);
        Assertions.assertEquals(result.getStart(), booking1.getStart());
        Assertions.assertEquals(result.getEnd(), booking1.getEnd());
        Assertions.assertEquals(result.getStatus(), booking1.getStatus());
        Assertions.assertEquals(result.getBooker().getId(), user1.getId());
        Assertions.assertEquals(result.getItem().getName(), item1.getName());
        Assertions.assertEquals(result.getItem().getId(), item1.getId());
    }
}
