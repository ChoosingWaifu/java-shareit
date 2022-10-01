package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<Item> searchItem(String text);

    Item addNewItem(Item item);

    List<Item> getItems(Long userId);

    Item getById(Long itemId);

    ItemDto updateItem(ItemDto item, Long itemId);

    void deleteItem(Long itemId);

}
