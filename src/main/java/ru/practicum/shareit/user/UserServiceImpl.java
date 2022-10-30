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
    public UserDto addNewUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public List<UserDto> getUsers() {
        return UserMapper.toUserDtoList(repository.findAll());
    }

    @Override
    public UserDto getById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("item not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User resultUser = repository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        Optional<String> name = Optional.ofNullable(user.getName());
        Optional<String> email = Optional.ofNullable(user.getEmail());
        name.ifPresent(resultUser::setName);
        email.ifPresent(resultUser::setEmail);
        return UserMapper.toUserDto(repository.save(resultUser));
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }
}
