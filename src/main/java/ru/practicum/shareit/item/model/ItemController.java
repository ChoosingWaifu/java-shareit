package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NullStatusException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private final UserRepository userRepository;

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toListItemDto(itemService.getItems(userId));
    }


    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.getById(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam Optional<String> text) {
        String parsedText = "";
        if (text.isPresent()) {
            parsedText = text.get().toLowerCase();
        }
        log.info("search {}", parsedText);
        return ItemMapper.toListItemDto(itemService.searchItem(parsedText));
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody @Valid Item item) throws UserNotFoundException, NullStatusException {
        if (!userRepository.getAllUsers().stream()
                .map(User::getId).collect(Collectors.toList()).contains(userId)) {
            throw new UserNotFoundException("user not found");
        }
        Optional<Boolean> status = Optional.ofNullable(item.getAvailable());
        if (status.isEmpty()){
            throw new NullStatusException("status can't be empty");
        }
        item.setOwner(userId);
        log.info("created item {}", item);
        return ItemMapper.toItemDto(itemService.addNewItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Long userId,
                      @RequestBody Item item, @PathVariable Long itemId) throws InsufficientRightsException {
        if (!Objects.equals(userId, itemService.getById(itemId).getOwner())) {
            throw new InsufficientRightsException("can't patch other user items");
        }
        log.info("item {} updated with {}", itemService.getById(itemId), item);
        return ItemMapper.toItemDto(itemService.updateItem(item, itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) throws InsufficientRightsException {
        if (!Objects.equals(userId, itemService.getById(itemId).getOwner())) {
            throw new InsufficientRightsException("can't delete other user items");
        }
        log.info("item {} deleted", itemService.getById(itemId));
        itemService.deleteItem(itemId);
    }
}