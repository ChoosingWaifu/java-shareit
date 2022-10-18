package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BookingWithStatusDto {

    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @Min(1)
    private Long itemId;
    private BookingStatus status;
}


