package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapperTest {

    private final User user1 = new User(1L, "user1", "user1@yandex.ru");

    private final ItemRequest itemRequest1 = new ItemRequest(null, "desc1", user1.getId(), null);

    private final ItemDto itemDto1 = new ItemDto(1L,"item1","desc1",true, itemRequest1.getId());

    @Test
    void toItemRequestTest() {
        ItemRequest result = ItemRequestMapper.toItemRequest(itemRequest1.getDescription(), itemRequest1.getUserId());
        Assertions.assertEquals(result.toString(), itemRequest1.toString());
    }

    @Test
    void toInfoDtoTest() {
        itemRequest1.setId(1L);
        itemRequest1.setCreated(LocalDateTime.now());
        ItemRequestInfoDto result = ItemRequestMapper.toInfoDto(itemRequest1, List.of(itemDto1));
        Assertions.assertEquals(result.getItems().toString(), List.of(itemDto1).toString());
        Assertions.assertEquals(result.getId(), itemRequest1.getId());
        Assertions.assertEquals(result.getCreated(), itemRequest1.getCreated());
        Assertions.assertEquals(result.getUserId(), itemRequest1.getUserId());
        Assertions.assertEquals(result.getDescription(), itemRequest1.getDescription());
    }

    @Test
    void toReturnDtoTest() {
        ItemRequestReturnDto result = ItemRequestMapper.toReturnDto(itemRequest1);
        Assertions.assertEquals(result.getId(), itemRequest1.getId());
        Assertions.assertEquals(result.getDescription(), itemRequest1.getDescription());
        Assertions.assertEquals(result.getCreated(), itemRequest1.getCreated());
    }
}
