package ru.practicum.shareit.item.model;

import java.util.List;

public interface ItemRepository {

    List<Item> searchItem(String text);

    List<Item> findByUserId(Long userId);

    Item getById(Long itemId);

    Item save(Item item);

    Item update(Item item);

    void deleteByItemId(Long itemId);
}

