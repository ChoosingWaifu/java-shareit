package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface UserService {

    User addNewUser(User user);

    List<User> getUsers();

    User getById(Long userId) throws NotFoundException;

    User updateUser(User user) throws NotFoundException;

    void deleteUser(Long userId);
}
