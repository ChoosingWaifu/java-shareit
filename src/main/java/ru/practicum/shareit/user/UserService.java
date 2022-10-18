package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    User addNewUser(UserDto userDto);

    List<User> getUsers();

    User getById(Long userId) throws NotFoundException;

    User updateUser(UserDto userDto) throws NotFoundException;

    void deleteUser(Long userId);
}
