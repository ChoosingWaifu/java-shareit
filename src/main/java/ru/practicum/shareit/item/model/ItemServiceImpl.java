package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    private static Long id = 1L;
    @Override
    public Item addNewItem(Item item) {
        item.setId(id++);
        return repository.save(item);
    }

    @Override
    public List<Item> getItems(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Item getById(Long itemId) {
        return repository.getById(itemId);
    }

    @Override
    public List<Item> searchItem(String text) {
        return repository.searchItem(text);
    }

    @Override
    public void deleteItem(Long itemId) {
        repository.deleteByItemId(itemId);
    }

    @Override
    public Item updateItem(Item item, Long itemId) {
        item.setId(itemId);
        return repository.update(item);
    }
}

