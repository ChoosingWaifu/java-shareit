package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final StringToStateConverter toStateConverter;

    @PostMapping
    public BookingPostDto postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody @Valid BookingDto bookingDto) throws NotFoundException, UnavailableItemException, ValidationException, InsufficientRightsException {
        Booking result = BookingMapper.toBooking(bookingDto, userId);
        log.info("posted booking request {}", result);
        return bookingService.post(result, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingPostDto approveRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) throws NotFoundException, InsufficientRightsException, ValidationException {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingPostDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) throws InsufficientRightsException, NotFoundException {
        return bookingService.getDtoById(bookingId, userId);
    }

    @GetMapping
    public List<BookingPostDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) throws NotFoundException {
        State result = toStateConverter.convert(state);
        return bookingService.getUserBookings(userId, result);
    }

    @GetMapping(value = "/owner")
    public List<BookingPostDto> getItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) throws NotFoundException {
        State result = toStateConverter.convert(state);
        return bookingService.getItemsBookings(userId, result);
    }

}
