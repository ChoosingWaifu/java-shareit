package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User addNewUser(User user) {
        return repository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User getById(Long userId) throws NotFoundException {
        return repository.findById(userId)
                .orElseThrow(()-> new NotFoundException("item not found"));
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        User resultUser = getById(user.getId());
        Optional<String> name = Optional.ofNullable(user.getName());
        Optional<String> email = Optional.ofNullable(user.getEmail());
        name.ifPresent(resultUser::setName);
        email.ifPresent(resultUser::setEmail);
        return repository.save(resultUser);
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }
}
