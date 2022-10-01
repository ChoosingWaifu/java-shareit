package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ItemRequest {

    private Long id;
    private String description;
    private Long userId;
    private LocalDateTime createTime;
}
