package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.pagination.PageFromRequest;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRequestRepository requestRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequest postRequest(String request, Long userId) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        ItemRequest result = ItemRequestMapper.toItemRequest(request, userId);
        log.info("post request {}, userId {}", request, userId);
        return requestRepository.save(result);
    }

    @Override
    public List<ItemRequestInfoDto> getUserRequests(Long userId) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        List<ItemRequest> requests = requestRepository.findByUserId(userId);
        List<ItemRequestInfoDto> dtoList = new ArrayList<>();
        for (ItemRequest request: requests) {
            List<Item> item = itemRepository.findByRequestId(request.getId());
            dtoList.add(ItemRequestMapper.toInfoDto(request, item));
        }
        return dtoList;
    }

    @Override
    public ItemRequestInfoDto getRequestById(Long requestId, Long userId) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("request not found"));
        List<Item> items = itemRepository.findByRequestId(request.getId());
        return ItemRequestMapper.toInfoDto(request, items);
    }

    @Override
    public List<ItemRequestInfoDto> getAllRequests(Long userId, Integer from, Integer size) {
        Pageable pageable = PageFromRequest.sortedOf(from, size, Sort.by("created").descending());
        List<ItemRequest> requests = new ArrayList<>(requestRepository.findAllByUserIdNot(userId, pageable));
        List<ItemRequestInfoDto> dtoList = new ArrayList<>();
        for (ItemRequest request: requests) {
            List<Item> item = itemRepository.findByRequestId(request.getId());
            dtoList.add(ItemRequestMapper.toInfoDto(request, item));
        }
        log.info("all requests from {}, size {}", from, size);
        return dtoList;
    }
}
