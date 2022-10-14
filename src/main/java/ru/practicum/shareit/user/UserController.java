package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.validation.Valid;
import java.util.List;
@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<User> get() {
        return service.getUsers();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) throws NotFoundException {
        return service.getById(userId);
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.info("created new user {}", user);
        return service.addNewUser(user);
    }

    @PatchMapping("/{userId}")
    public User patchUser(@RequestBody User user, @PathVariable Long userId) throws NotFoundException {
        user.setId(userId);
        log.info("patch {}, {}", userId, user);
        return service.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }
}