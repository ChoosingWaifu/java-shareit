package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private static Long id = 1L;

    @Override
    public User addNewUser(User user) {
        user.setId(id++);
        return repository.addNewUser(user);
    }

    @Override
    public List<User> getUsers() {
        return repository.getAllUsers();
    }

    @Override
    public User getById(long userId) {
        return repository.getById(userId);
    }

    @Override
    public User updateUser(User user) {
        return repository.updateUser(user);
    }

    @Override
    public void deleteUser(long userId) {
        repository.deleteUser(userId);
    }
}
