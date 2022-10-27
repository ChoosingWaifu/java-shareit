package ru.practicum.shareit.item.model;

import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NullStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    Item addNewItem(Item item, Long userId) throws NotFoundException, NullStatusException;

    List<ItemWithBookingDto> getItems(Long userId, Integer from, Integer size) throws NotFoundException;

    ItemWithBookingDto getByIdWithBooking(Long itemId, Long userId) throws NotFoundException;

    Item getById(Long itemId) throws NotFoundException;

    ItemDto updateItem(ItemDto item, Long itemId, Long userId) throws NotFoundException, InsufficientRightsException;

    CommentAuthorNameDto postComment(String comment, Long itemId, Long authorId) throws ValidationException, NotFoundException;

    void deleteItem(Long itemId, Long userId) throws NotFoundException, InsufficientRightsException;

}
