package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Valid
@RequiredArgsConstructor
@AllArgsConstructor
public class UserValidateDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;

}
