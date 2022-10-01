package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Getter
    @Setter
    private static Long id = 1L;

    @Override
    public User addNewUser(User user) {
        Long increment = getId();
        user.setId(increment);
        setId(++increment);
        return repository.addNewUser(user);
    }

    @Override
    public List<User> getUsers() {
        return repository.getAllUsers();
    }

    @Override
    public User getById(Long userId) {
        return repository.getById(userId);
    }

    @Override
    public User updateUser(User user) {
        return repository.updateUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteUser(userId);
    }
}
