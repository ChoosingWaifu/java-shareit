package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemWithBookingDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    private BookingForItem lastBooking;
    private BookingForItem nextBooking;

    private List<CommentAuthorNameDto> comments;

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class BookingForItem {
        private Long id;
        private Long bookerId;
    }

}
