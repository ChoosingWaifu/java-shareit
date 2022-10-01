package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    @Getter
    @Setter
    private static Long id = 1L;

    @Override
    public Item addNewItem(Item item) {
        Long increment = getId();
        item.setId(increment);
        setId(++increment);
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
    public ItemDto updateItem(ItemDto itemDto, Long itemId) {
        Item resultItem = getById(itemId);
        itemDto.setId(itemId);
        Optional<String> name = Optional.ofNullable(itemDto.getName());
        Optional<String> description = Optional.ofNullable(itemDto.getDescription());
        Optional<Boolean> available = Optional.ofNullable(itemDto.getAvailable());
        name.ifPresent(resultItem::setName);
        description.ifPresent(resultItem::setDescription);
        available.ifPresent(resultItem::setAvailable);
        return ItemMapper.toItemDto(repository.update(resultItem));
    }
}

