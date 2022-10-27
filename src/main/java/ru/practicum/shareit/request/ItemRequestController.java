package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequest postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestBody @Valid ItemRequestDto itemRequestDto) throws NotFoundException {
        String request = itemRequestDto.getDescription();
        log.info("post item request controller");
        return service.postRequest(request, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) throws NotFoundException {
        return service.getRequestById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestInfoDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        return service.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "20") Integer size) {
        return service.getAllRequests(userId, from, size);
    }

}
