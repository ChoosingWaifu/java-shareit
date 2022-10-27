package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingDto> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "20") Integer size) throws NotFoundException {
        return itemService.getItems(userId, from, size);
    }


    @GetMapping("/{itemId}")
    public ItemWithBookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId) throws NotFoundException {
        return itemService.getByIdWithBooking(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam Optional<String> text,
                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                @Positive @RequestParam(defaultValue = "20") Integer size) {
        String parsedText;
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        parsedText = text.get().toLowerCase();
        log.info("search {}", parsedText);
        if (parsedText.isEmpty()) {
            return new ArrayList<>();
        }
        return itemService.searchItem(parsedText, from, size);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid ItemDto itemDto) throws NotFoundException, NullStatusException {
        Item result = ItemMapper.toItem(itemDto, userId);
        log.info("created item {}", result);
        return ItemMapper.toItemDto(itemService.addNewItem(result, userId));
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
        log.info("item {} updated with {}", itemService.getById(itemId), itemDto);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) throws InsufficientRightsException, NotFoundException {
        log.info("item {} deleted", itemService.getById(itemId));
        itemService.deleteItem(itemId, userId);
    }
}