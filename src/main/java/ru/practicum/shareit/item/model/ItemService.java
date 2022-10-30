package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    Item addNewItem(Item item, Long userId);

    List<ItemWithBookingDto> getItems(Long userId, Integer from, Integer size);

    ItemWithBookingDto getByIdWithBooking(Long itemId, Long userId);

    Item getById(Long itemId);

    ItemDto updateItem(ItemDto item, Long itemId, Long userId);

    CommentAuthorNameDto postComment(String comment, Long itemId, Long authorId);

    void deleteItem(Long itemId, Long userId);

}
