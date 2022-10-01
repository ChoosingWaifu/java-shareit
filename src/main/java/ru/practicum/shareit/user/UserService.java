package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    User addNewUser(User user);

    List<User> getUsers();

    User getById(Long userId);

    User updateUser(User user);

    void deleteUser(Long userId);
}
