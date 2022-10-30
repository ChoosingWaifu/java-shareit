package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Valid
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequestInfoDto {

    private Long id;

    private String description;

    private Long userId;

    private LocalDateTime created;

    private List<ItemDto> items;
}
