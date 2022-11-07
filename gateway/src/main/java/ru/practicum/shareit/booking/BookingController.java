package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingValidateDto;
import ru.practicum.shareit.booking.dto.StringToStateConverter;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final StringToStateConverter converter;

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid BookingValidateDto bookingDto) {
        log.info("gateway posted booking request {}", bookingDto);
        return client.postRequest(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> approveRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam Boolean approved,
                                                 @PathVariable long bookingId) {
        log.info("gateway approve booking request userid {}, approved {}", userId, approved);
        return client.approveRequest(bookingId, approved, userId);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        log.info("gateway get booking by id {}, userId {}", bookingId, userId);
        return client.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        BookingState enumState = converter.convert(state);
        log.info("gateway getUserBookings state {}, from {} , size {}", state, from, size);
        assert enumState != null;
        return client.getUserBookings(userId, enumState, from, size);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "20") Integer size) {
        BookingState enumState = converter.convert(state);
        log.info("gateway getItemsBookings state {}, from {} , size {}", state, from, size);
        assert enumState != null;
        return client.getItemsBookings(userId, enumState, from, size);
    }

}
