package ru.practicum.shareit.item.model;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {

    List<Item> searchItem(String text);

    Item addNewItem(Item item);

    List<ItemWithBookingDto> getItems(Long userId) throws NotFoundException;

    ItemWithBookingDto getByIdWithBooking(Long itemId, Long userId) throws NotFoundException;

    Item getById(Long itemId) throws NotFoundException;

    ItemDto updateItem(ItemDto item, Long itemId) throws NotFoundException;

    CommentAuthorNameDto postComment(String comment, Long itemId, Long authorId) throws ValidationException, NotFoundException;

    void deleteItem(Long itemId);

}
