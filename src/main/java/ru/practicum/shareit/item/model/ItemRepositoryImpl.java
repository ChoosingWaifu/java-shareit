package ru.practicum.shareit.item.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long,Item> items = new HashMap<>();

    @Override
    public List<Item> findByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> empty = new ArrayList<>();
        log.info("searchItem {}", items.values().stream().map(Item::getName).collect(Collectors.toList()));
        if (text.equals("")) {
            return empty;
        }
        return items.values()
                .stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());

    }

    @Override
    public Item save(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item resultItem) {
        items.put(resultItem.getId(), resultItem);
        return resultItem;
    }

    @Override
    public void deleteByItemId(Long itemId) {
        items.remove(itemId);
    }
}
