package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.pagination.PageFromRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final StringToStateConverter toStateConverter;

    @Override
    @Transactional
    public BookingPostDto post(BookingDto bookingDto, Long userId) {
        Booking booking = BookingMapper.toBooking(bookingDto, userId);
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        if (userId.equals(item.getOwner())) {
            throw new InsufficientRightsException("cant book your own item");
        }
        if (!item.getAvailable()) {
            throw new UnavailableItemException("item unavailable");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("time format exception");
        }
        log.info("posted booking request {}", booking);
        return BookingMapper.toPostDto(repository.save(booking), item, user);
    }

    @Override
    public BookingPostDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getById(bookingId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("cant patch approved booking");
        }
        User booker = userRepository.findById(booking.getBooker())
                .orElseThrow(() -> new NotFoundException("user not found"));
        Item bookedItem = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        if (!bookedItem.getOwner().equals(userId)) {
            throw new InsufficientRightsException("can't approve others item bookings");
        }
        BookingStatus status = approved.equals(true) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        log.info("approved booking request {}", booking);
        return BookingMapper.toPostDto(repository.save(booking), bookedItem, booker);
    }

    @Override
    public Booking getById(Long bookingId) {
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        } else throw new NotFoundException("booking not found");
    }

    @Override
    public BookingPostDto getDtoById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        Booking result = getById(bookingId);
        Item item = itemRepository.findById(result.getItemId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        User booker = userRepository.findById(result.getBooker())
                .orElseThrow(() -> new NotFoundException("user not found"));
        if (!userId.equals(result.getBooker()) &&
                !userId.equals(item.getOwner())) {
            throw new InsufficientRightsException("not enough rights");
        }
        log.info("get booking {}", result);
        return BookingMapper.toPostDto(result, item, booker);
    }

    @Override
    public List<BookingPostDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        State result = toStateConverter.convert(state);
        Pageable pageable = PageFromRequest.sortedOf(from, size, Sort.by("end").descending());
        List<Booking> bookingList = repository.findByBooker(userId, pageable);
        List<BookingPostDto> dtoList = new ArrayList<>();
        for (Booking booking: bookingList) {
            Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new NotFoundException("item not found"));
            dtoList.add(BookingMapper.toPostDto(booking, item, booker));
        }
        assert result != null;
        log.info("get user bookings {}", bookingStateFilter(result, dtoList));
        return bookingStateFilter(result, dtoList);
    }

    @Override
    public List<BookingPostDto> getItemsBookings(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        State result = toStateConverter.convert(state);
        List<Item> items = itemRepository.findByOwner(userId);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        Pageable pageable = PageFromRequest.sortedOf(from, size, Sort.by("end").descending());
        List<Booking> bookingList = new ArrayList<>();
        List<BookingPostDto> dtoList = new ArrayList<>();
        for (Long itemId: itemsId) {
            bookingList.addAll(repository.findByItemId(itemId, pageable));
        }
        for (Booking booking: bookingList) {
            Item item = itemRepository.findById(booking.getItemId())
                    .orElseThrow(() -> new NotFoundException("item not found"));
            User booker = userRepository.findById(booking.getBooker())
                    .orElseThrow(() -> new NotFoundException("user not found"));
            dtoList.add(BookingMapper.toPostDto(booking, item, booker));
        }
        assert result != null;
        log.info("get items bookings {}", bookingStateFilter(result, dtoList));
        return bookingStateFilter(result, dtoList);
    }

    public List<BookingPostDto> bookingStateFilter(State state, List<BookingPostDto> dtoList) {
        BookingStatus filter = null;
        switch (state) {
            case ALL:
                return new ArrayList<>(dtoList);
            case WAITING:
                filter = BookingStatus.WAITING;
                break;
            case REJECTED:
                filter = BookingStatus.REJECTED;
                break;
            case PAST:
                return dtoList.stream()
                        .filter(o -> o.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case FUTURE:
                return dtoList.stream()
                        .filter(o -> o.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case CURRENT:
                return dtoList.stream()
                        .filter(o -> o.getStart().isBefore(LocalDateTime.now()))
                        .filter(o -> o.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());

        }
        BookingStatus finalFilter = filter;
        return dtoList.stream()
                .filter(o -> o.getStatus() == finalFilter)
                .collect(Collectors.toList());

    }
}
