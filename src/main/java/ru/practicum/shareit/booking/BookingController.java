package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingPostDto postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Valid BookingDto bookingDto) {
        log.info("posted booking request {}", bookingDto);
        return bookingService.post(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingPostDto approveRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingPostDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getDtoById(bookingId, userId);
    }

    @GetMapping
    public List<BookingPostDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "20") Integer size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping(value = "/owner")
    public List<BookingPostDto> getItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("state {}, from {} , size {}", state, from, size);
        return bookingService.getItemsBookings(userId, state, from, size);
    }

}
