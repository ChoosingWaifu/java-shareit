package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Override
    @Transactional
    public BookingPostDto post(Booking booking, Long userId) throws NotFoundException, InsufficientRightsException, UnavailableItemException, ValidationException {
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
    public BookingPostDto approve(Long userId, Long bookingId, Boolean approved) throws NotFoundException, InsufficientRightsException, ValidationException {
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
    public Booking getById(Long bookingId) throws NotFoundException {
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        } else throw new NotFoundException("booking not found");
    }

    @Override
    public BookingPostDto getDtoById(Long bookingId, Long userId) throws NotFoundException, InsufficientRightsException {
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
    public List<BookingPostDto> getUserBookings(Long userId, State state) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        List<Booking> bookingList = repository.findByBooker(userId);
        List<BookingPostDto> dtoList = new ArrayList<>();
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        for (Booking booking: bookingList) {
            Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new NotFoundException("item not found"));
            dtoList.add(BookingMapper.toPostDto(booking, item, booker));
        }
        log.info("get user bookings {}", bookingStateFilter(state, dtoList));
        return bookingStateFilter(state, dtoList);
    }

    @Override
    public List<BookingPostDto> getItemsBookings(Long userId, State state) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        List<Item> items = itemRepository.findByOwner(userId);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookingList = new ArrayList<>();
        List<BookingPostDto> dtoList = new ArrayList<>();
        for (Long itemId: itemsId) {
            bookingList.addAll(repository.findByItemId(itemId));
        }
        for (Booking booking: bookingList) {
            Item item = itemRepository.findById(booking.getItemId())
                    .orElseThrow(() -> new NotFoundException("item not found"));
            User booker = userRepository.findById(booking.getBooker())
                    .orElseThrow(() -> new NotFoundException("user not found"));
            dtoList.add(BookingMapper.toPostDto(booking, item, booker));
        }
        log.info("get items bookings {}", bookingStateFilter(state, dtoList));
        return bookingStateFilter(state, dtoList);
    }

    public List<BookingPostDto> bookingStateFilter(State state, List<BookingPostDto> dtoList) {
        BookingStatus filter = null;
        switch (state) {
            case ALL:
                return dtoList.stream()
                        .sorted(Comparator.comparing(BookingPostDto::getEnd).reversed())
                        .collect(Collectors.toList());
            case WAITING:
                filter = BookingStatus.WAITING;
                break;
            case REJECTED:
                filter = BookingStatus.REJECTED;
                break;
            case PAST:
                return dtoList.stream()
                        .filter(o -> o.getEnd().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(BookingPostDto::getEnd).reversed())
                        .collect(Collectors.toList());
            case FUTURE:
                return dtoList.stream()
                        .filter(o -> o.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(BookingPostDto::getEnd).reversed())
                        .collect(Collectors.toList());
            case CURRENT:
                return dtoList.stream()
                        .filter(o -> o.getStart().isBefore(LocalDateTime.now()))
                        .filter(o -> o.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(BookingPostDto::getEnd).reversed())
                        .collect(Collectors.toList());

        }
        BookingStatus finalFilter = filter;
        return dtoList.stream()
                .filter(o -> o.getStatus() == finalFilter)
                .sorted(Comparator.comparing(BookingPostDto::getEnd).reversed())
                .collect(Collectors.toList());

    }
}
