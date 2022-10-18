package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {

        private Long id;
        @NotBlank
        private String name;
        @NotBlank
        private String description;
        private Boolean available;
}
