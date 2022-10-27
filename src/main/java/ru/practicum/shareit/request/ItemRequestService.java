package ru.practicum.shareit.request;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequest postRequest(String request, Long userId) throws NotFoundException;

    List<ItemRequestInfoDto> getUserRequests(Long userId) throws NotFoundException;

    ItemRequestInfoDto getRequestById(Long requestId, Long userId) throws NotFoundException;

    List<ItemRequestInfoDto> getAllRequests(Long userId, Integer from, Integer size);
}
