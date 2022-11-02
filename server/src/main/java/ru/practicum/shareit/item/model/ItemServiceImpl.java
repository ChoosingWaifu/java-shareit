package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NullStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.pagination.PageFromRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;


    @Override
    public Item addNewItem(Item item, Long userId) {
        if (!userRepository.findAll().stream()
                .map(User::getId).collect(Collectors.toList()).contains(userId)) {
            throw new NotFoundException("user not found");
        }
        Optional<Boolean> status = Optional.ofNullable(item.getAvailable());
        if (status.isEmpty()) {
            throw new NullStatusException("status can't be empty");
        }
        return repository.save(item);
    }

    @Override
    public List<ItemWithBookingDto> getItems(Long userId, Integer from, Integer size) {
        Pageable pageable = PageFromRequest.of(from, size);
        List<ItemWithBookingDto> result = new ArrayList<>();
        List<Item> itemList = repository.findByOwner(userId, pageable);
        for (Item item: itemList) {
            result.add(toItemWithBookingDto(item));
        }
        return result;
    }

    @Override
    public Item getById(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));
    }

    @Override
    public ItemWithBookingDto getByIdWithBooking(Long itemId, Long userId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));
        if (!userId.equals(item.getOwner())) {
            List<Comment> comments = commentRepository.findByItem(item.getId());
            return ItemMapper.toWithBookingDto(item, null, null, commentAuthorNameDto(comments));
        }
        return toItemWithBookingDto(item);
    }

    @Override
    public CommentAuthorNameDto postComment(String comment, Long itemId, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        List<Booking> bookings = bookingRepository.findByItemId(itemId).stream()
                .filter(o -> o.getStatus().equals(BookingStatus.APPROVED))
                .filter(o -> o.getStart().isBefore(LocalDateTime.now()))
                .filter(o -> o.getBooker().equals(authorId))
                .collect(Collectors.toList());
        log.info("post com service impl item id {}, author id {}, bookings {}", itemId, authorId, bookings);
        if (bookings.isEmpty()) {
            throw new ValidationException("cant comment unbooked item");
        }
        Comment result = commentRepository.save(CommentMapper.toComment(comment, itemId, authorId));
        return CommentMapper.toAuthorNameDto(result, author.getName());
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        Pageable pageable = PageFromRequest.of(from, size);
        return ItemMapper.toListItemDto(repository.findByNameOrDescriptionContainingIgnoreCaseAndAvailable(text, text, true, pageable));
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        if (!Objects.equals(userId, getById(itemId).getOwner())) {
            throw new InsufficientRightsException("can't delete other user items");
        }
        repository.deleteById(itemId);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        if (!Objects.equals(userId, getById(itemId).getOwner())) {
            throw new InsufficientRightsException("can't patch other user items");
        }
        Item resultItem = getById(itemId);
        itemDto.setId(itemId);
        Optional<String> name = Optional.ofNullable(itemDto.getName());
        Optional<String> description = Optional.ofNullable(itemDto.getDescription());
        Optional<Boolean> available = Optional.ofNullable(itemDto.getAvailable());
        name.ifPresent(resultItem::setName);
        description.ifPresent(resultItem::setDescription);
        available.ifPresent(resultItem::setAvailable);
        return ItemMapper.toItemDto(repository.save(resultItem));
    }

    private List<CommentAuthorNameDto> commentAuthorNameDto(List<Comment> comments) {
        List<CommentAuthorNameDto> dtoList = new ArrayList<>();
        for (Comment comment: comments) {
            User author = userRepository.findById(comment.getAuthor())
                    .orElseThrow(() -> new NotFoundException("user not found"));
            dtoList.add(CommentMapper.toAuthorNameDto(comment, author.getName()));
        }
        return dtoList;
    }

    private ItemWithBookingDto toItemWithBookingDto(Item item) {
        List<Booking> itemBookings = bookingRepository.findByItemIdAndStatus(item.getId(), BookingStatus.APPROVED);
        log.info("item {} List Bookings {}", item, bookingRepository.findAll());
        List<Booking> nextBookingList = itemBookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStart))
                .limit(1)
                .collect(Collectors.toList());
        List<Booking> lastBookingList = itemBookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .limit(1)
                .collect(Collectors.toList());
        Booking nextBooking = nextBookingList.isEmpty() ? null : nextBookingList.get(0);
        Booking lastBooking = lastBookingList.isEmpty() ? null : lastBookingList.get(0);
        List<Comment> comments = commentRepository.findByItem(item.getId());
        return ItemMapper.toWithBookingDto(item, lastBooking, nextBooking, commentAuthorNameDto(comments));
    }
}

