package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User addNewUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public User getById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User updateUser(User user) {
        User resultUser = users.get(user.getId());
        Optional<String> userName = Optional.ofNullable(user.getName());
        Optional<String> email = Optional.ofNullable(user.getEmail());
        userName.ifPresent(resultUser::setName);
        email.ifPresent(resultUser::setEmail);
        users.put(resultUser.getId(), resultUser);
        return resultUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }
}
