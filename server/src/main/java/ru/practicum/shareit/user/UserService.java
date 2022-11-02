package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addNewUser(UserDto userDto);

    List<UserDto> getUsers();

    UserDto getById(Long userId);

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long userId);
}
