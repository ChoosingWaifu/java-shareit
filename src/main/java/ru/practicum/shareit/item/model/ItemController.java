package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NullStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.Valid;
import java.util.ArrayList;
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
    public List<ItemWithBookingDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemService.getItems(userId);
    }


    @GetMapping("/{itemId}")
    public ItemWithBookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId) throws NotFoundException {
        return itemService.getByIdWithBooking(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam Optional<String> text) {
        String parsedText;
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        parsedText = text.get().toLowerCase();
        log.info("search {}", parsedText);
        if(parsedText.isEmpty()) {
            return new ArrayList<>();
        }
        return ItemMapper.toListItemDto(itemService.searchItem(parsedText));
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody @Valid ItemDto itemDto) throws NotFoundException, NullStatusException {
        if (!userRepository.findAll().stream()
                .map(User::getId).collect(Collectors.toList()).contains(userId)) {
            throw new NotFoundException("user not found");
        }
        Optional<Boolean> status = Optional.ofNullable(itemDto.getAvailable());
        if (status.isEmpty()) {
            throw new NullStatusException("status can't be empty");
        }
        Item result = ItemMapper.toItem(itemDto, userId);
        log.info("created item {}", result);
        return ItemMapper.toItemDto(itemService.addNewItem(result));
    }

    @PostMapping("/{itemId}/comment")
    public CommentAuthorNameDto postComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody @Valid CommentDto commentDto,
                                            @PathVariable Long itemId) throws ValidationException, NotFoundException {
        String comment = commentDto.getText();
        return itemService.postComment(comment, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @RequestBody ItemDto itemDto,
                         @PathVariable Long itemId) throws InsufficientRightsException, NotFoundException {
        if (!Objects.equals(userId, itemService.getById(itemId).getOwner())) {
            throw new InsufficientRightsException("can't patch other user items");
        }
        log.info("item {} updated with {}", itemService.getById(itemId), itemDto);
        return itemService.updateItem(itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) throws InsufficientRightsException, NotFoundException {
        if (!Objects.equals(userId, itemService.getById(itemId).getOwner())) {
            throw new InsufficientRightsException("can't delete other user items");
        }
        log.info("item {} deleted", itemService.getById(itemId));
        itemService.deleteItem(itemId);
    }
}