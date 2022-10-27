package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addNewUser(UserDto userDto);

    List<UserDto> getUsers();

    UserDto getById(Long userId) throws NotFoundException;

    UserDto updateUser(UserDto userDto) throws NotFoundException;

    void deleteUser(Long userId);
}
