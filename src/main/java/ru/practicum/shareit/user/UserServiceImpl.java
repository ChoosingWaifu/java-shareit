package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User addNewUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return repository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User getById(Long userId) throws NotFoundException {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("item not found"));
    }

    @Override
    public User updateUser(UserDto userDto) throws NotFoundException {
        User user = UserMapper.toUser(userDto);
        User resultUser = getById(userDto.getId());
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
