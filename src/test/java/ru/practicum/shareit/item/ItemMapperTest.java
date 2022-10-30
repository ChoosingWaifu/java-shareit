package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemMapperTest {

    private final CommentAuthorNameDto commentAuthorName = new CommentAuthorNameDto(null, "text", "authorName", null);
    private final User user1 = new User(1L, "user1", "user1@yandex.ru");
    private final Item item1 = new Item(1L,"item1","desc1",true,1L,null, new ArrayList<>());
    private final Booking booking1 = new Booking(1L, null, null, item1.getId(), user1.getId(), BookingStatus.APPROVED);
    private final Booking booking2 = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), item1.getId(), user1.getId(), BookingStatus.APPROVED);
    private final ItemDto itemDto1 = new ItemDto(1L,"item1","desc1",true,null);

    @Test
    void toItemDtoTest() {
        ItemDto result = ItemMapper.toItemDto(item1);
        Assertions.assertEquals(result.toString(), itemDto1.toString());
    }

    @Test
    void toItemTest() {
        Item result = ItemMapper.toItem(itemDto1, item1.getOwner());
        result.setId(item1.getId());
        Assertions.assertEquals(result.toString(), item1.toString());
    }

    @Test
    void toListItemDtoTest() {
        List<ItemDto> result = ItemMapper.toListItemDto(List.of(item1));
        Assertions.assertEquals(result.toString(), List.of(itemDto1).toString());
    }

    @Test
    void toWithBookingDtoTest() {
        ItemWithBookingDto result1 = ItemMapper.toWithBookingDto(item1, booking1, booking2, List.of(commentAuthorName));
        Assertions.assertEquals(result1.getLastBooking().getBookerId(), booking1.getBooker());
        Assertions.assertEquals(result1.getLastBooking().getId(), booking1.getId());
        Assertions.assertEquals(result1.getNextBooking().getBookerId(), booking2.getBooker());
        Assertions.assertEquals(result1.getNextBooking().getId(), booking2.getId());
        Assertions.assertEquals(result1.getComments(), List.of(commentAuthorName));
        Assertions.assertEquals(result1.getName(), item1.getName());
        Assertions.assertEquals(result1.getId(), item1.getId());

        ItemWithBookingDto result2 = ItemMapper.toWithBookingDto(item1, null, null, List.of(commentAuthorName));
        Assertions.assertNull(result2.getLastBooking());
        Assertions.assertNull(result2.getNextBooking());
        Assertions.assertEquals(result1.getComments(), List.of(commentAuthorName));
        Assertions.assertEquals(result1.getName(), item1.getName());
        Assertions.assertEquals(result1.getId(), item1.getId());
    }

}
