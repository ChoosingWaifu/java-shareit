package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User addNewUser(User user);

    List<User> getAllUsers();

    User getById(long userId);

    User updateUser(User user);

    void deleteUser(long userId);
}
