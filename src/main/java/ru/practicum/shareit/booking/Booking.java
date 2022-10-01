package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class Booking {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Long userId;
    private BookingStatus status;
}
