package ru.practicum.shareit.item.model;

import java.util.List;

public interface ItemService {

    List<Item> searchItem(String text);

    Item addNewItem(Item item);

    List<Item> getItems(Long userId);

    Item getById(Long itemId);

    Item updateItem(Item item, Long itemId);

    void deleteItem(Long itemId);

}
