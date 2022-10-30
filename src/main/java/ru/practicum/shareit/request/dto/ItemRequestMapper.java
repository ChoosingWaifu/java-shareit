package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(String text, Long userId) {
        return new ItemRequest(
                null,
                text,
                userId,
                null
                );
    }

    public static ItemRequestInfoDto toInfoDto(ItemRequest request, List<ItemDto> items) {
        return new ItemRequestInfoDto(
                request.getId(),
                request.getDescription(),
                request.getUserId(),
                request.getCreated(),
                items
        );
    }

    public static ItemRequestReturnDto toReturnDto(ItemRequest request) {
        return new ItemRequestReturnDto(
                request.getId(),
                request.getDescription(),
                request.getCreated()
        );
    }
}
