package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final StringToStateConverter toStateConverter;

    @PostMapping
    public BookingPostDto postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody @Valid BookingDto bookingDto) throws NotFoundException, UnavailableItemException, ValidationException, InsufficientRightsException {
        Booking result = BookingMapper.toBooking(bookingDto, userId);
        Item item = itemRepository.findById(result.getItemId())
                .orElseThrow(()-> new NotFoundException("item not found"));
        if (userId.equals(item.getOwner())) {
            throw new InsufficientRightsException("cant book your own item");
        }
        if (!item.getAvailable()) {
            throw new UnavailableItemException("item unavailable");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("time format exception");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        log.info("posted booking request {}", result);
        return BookingMapper.toPostDto(bookingService.post(result), item, user);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingPostDto approveRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) throws NotFoundException, InsufficientRightsException, ValidationException {
        Booking booking = bookingService.getById(bookingId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("cant patch approved booking");
        }
        User booker = userRepository.findById(booking.getBooker())
                .orElseThrow(()-> new NotFoundException("user not found"));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(()-> new NotFoundException("item not found"));
        return BookingMapper.toPostDto(bookingService.approve(userId, bookingId, approved), item, booker);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingPostDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) throws InsufficientRightsException, NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        Booking result = bookingService.getById(bookingId);
        Item item = itemRepository.findById(result.getItemId())
                .orElseThrow(()-> new NotFoundException("item not found"));
        User booker = userRepository.findById(result.getBooker())
                .orElseThrow(()-> new NotFoundException("user not found"));
        if (!userId.equals(result.getBooker()) &&
            !userId.equals(item.getOwner())) {
            throw new InsufficientRightsException("not enough rights");
        }
        return BookingMapper.toPostDto(result, item, booker);
    }

    @GetMapping
    public List<BookingPostDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        State result = toStateConverter.convert(state);
        return bookingService.getUserBookings(userId, result);
    }

    @GetMapping(value = "/owner")
    public List<BookingPostDto> GetItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        State result = toStateConverter.convert(state);
        return bookingService.getItemsBookings(userId, result);
    }

}
