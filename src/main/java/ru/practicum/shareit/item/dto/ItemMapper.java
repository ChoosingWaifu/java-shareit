package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static List<ItemDto> toListItemDto(List<Item> itemList) {
       List<ItemDto> result = new ArrayList<>();
       for(Item item: itemList){
           result.add(toItemDto(item));
       }
        return result;
    }

}
