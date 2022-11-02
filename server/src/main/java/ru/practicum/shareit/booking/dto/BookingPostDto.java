package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BookingPostDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserInfo booker;
    private ItemInfo item;
    private BookingStatus status;


    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class ItemInfo {
        private Long id;
        private String name;
    }

    @Override
    public String toString() {
        return "BookingPostDto{" +
                "id=" + id +
                ", start=" + start +
                ", status=" + status +
                '}';
    }
}
