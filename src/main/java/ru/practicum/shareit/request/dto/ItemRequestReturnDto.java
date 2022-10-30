package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Getter
@Setter
@Valid
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequestReturnDto {

    private Long id;

    private String description;

    private LocalDateTime created;

}

