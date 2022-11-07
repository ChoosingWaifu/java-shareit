package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Valid
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequestValidateDto {

    @NotNull
    private String description;
}
