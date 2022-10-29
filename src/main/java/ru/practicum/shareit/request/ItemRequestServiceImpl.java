package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.pagination.PageFromRequest;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRequestRepository requestRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest postRequest(String request, Long userId) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        ItemRequest result = ItemRequestMapper.toItemRequest(request, userId);
        log.info("post request {}, userId {}", request, userId);
        return requestRepository.save(result);
    }

    @Override
    @Transactional
    public ItemRequestInfoDto getRequestById(Long requestId, Long userId) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("request not found"));
        List<Item> items = itemRepository.findByRequestId(request.getId());
        List<ItemDto> result = new ArrayList<>();
        for (Item i : items) {
            result.add(ItemMapper.toItemDto(i));
        }
        return ItemRequestMapper.toInfoDto(request, result);
    }

    @Override
    @Transactional
    public List<ItemRequestInfoDto> getUserRequests(Long userId) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        List<ItemRequest> requests = requestRepository.findByUserId(userId);
        List<Item> items = itemRepository.findAllByRequestIdNotNull();
        return toItemRequestInfoDto(requests, items);
    }

    @Override
    @Transactional
    public List<ItemRequestInfoDto> getAllRequests(Long userId, Integer from, Integer size) {
        Pageable pageable = PageFromRequest.sortedOf(from, size, Sort.by("created").descending());
        List<ItemRequest> requests = new ArrayList<>(requestRepository.findAllByUserIdNot(userId, pageable));
        List<Item> items = itemRepository.findAllByRequestIdNotNull();
        return toItemRequestInfoDto(requests, items);
    }

    private List<ItemRequestInfoDto> toItemRequestInfoDto(List<ItemRequest> requests, List<Item> items) {
        List<ItemRequestInfoDto> dtoList = new ArrayList<>();
        List<ItemDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> requestedItems = items.stream().filter(o -> Objects.equals(o.getRequestId(), request.getId())).collect(Collectors.toList());
            for (Item i : requestedItems) {
                result.add(ItemMapper.toItemDto(i));
            }
            dtoList.add(ItemRequestMapper.toInfoDto(request, result));
        }
        return dtoList.stream().sorted(Comparator.comparing(ItemRequestInfoDto::getCreated).reversed()).collect(Collectors.toList());
    }
}