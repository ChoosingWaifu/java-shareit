package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> get() {
        return service.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) throws NotFoundException {
        return service.getById(userId);
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.info("created new user {}", userDto);
        return service.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody UserDto userDto, @PathVariable Long userId) throws NotFoundException {
        userDto.setId(userId);
        log.info("patch {}, {}", userId, userDto);
        return service.updateUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }
}