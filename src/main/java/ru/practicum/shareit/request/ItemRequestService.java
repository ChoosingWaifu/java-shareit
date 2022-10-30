package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequest postRequest(String request, Long userId);

    List<ItemRequestInfoDto> getUserRequests(Long userId);

    ItemRequestInfoDto getRequestById(Long requestId, Long userId);

    List<ItemRequestInfoDto> getAllRequests(Long userId, Integer from, Integer size);
}
