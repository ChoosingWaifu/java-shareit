package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking post(Booking booking) {
        return repository.save(booking);
    }

    @Override
    public Booking approve(Long userId, Long bookingId, Boolean approved) throws NotFoundException, InsufficientRightsException {
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("booking not found");
        }
        Booking result = booking.get();
        Item bookedItem = itemRepository.findById(result.getItemId())
                .orElseThrow(()-> new NotFoundException("item not found"));
        if (!bookedItem.getOwner().equals(userId)) {
            throw new InsufficientRightsException("can't approve others item bookings");
        }
        BookingStatus status = approved.equals(true) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        result.setStatus(status);
        return repository.save(result);
    }

    @Override
    public Booking getById(Long bookingId) throws NotFoundException {
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        } else throw new NotFoundException("booking not found");
    }

    @Override
    public List<BookingPostDto> getUserBookings(Long userId, State state) throws NotFoundException {
        List<Booking> bookingList = repository.findByBooker(userId);
        List<BookingPostDto> dtoList = new ArrayList<>();
        User booker = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("user not found"));
        for (Booking booking: bookingList) {
            Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(()-> new NotFoundException("item not found"));
            dtoList.add(BookingMapper.toPostDto(booking, item, booker));
        }
        return bookingStateFilter(state, dtoList);
    }

    @Override
    public List<BookingPostDto> getItemsBookings(Long userId, State state) throws NotFoundException {
        List <Item> items = itemRepository.findByOwner(userId);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookingList = new ArrayList<>();
        List<BookingPostDto> dtoList = new ArrayList<>();
        for (Long itemId: itemsId) {
            bookingList.addAll(repository.findByItemId(itemId));
        }
        for (Booking booking: bookingList) {
            Item item = itemRepository.findById(booking.getItemId())
                    .orElseThrow(()-> new NotFoundException("item not found"));
            User booker = userRepository.findById(booking.getBooker())
                    .orElseThrow(()->new NotFoundException("user not found"));
            dtoList.add(BookingMapper.toPostDto(booking, item, booker));
        }
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
