package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.DuplicateEmailException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
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
    public User getById(@PathVariable Long userId) {
        return service.getById(userId);
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) throws DuplicateEmailException {
        List<String> emails = service.getUsers().stream()
                .map(User::getEmail).collect(Collectors.toList());
        if (emails.contains(user.getEmail())) {
            throw new DuplicateEmailException("Duplicate email");
        }
        log.info("created new user {}", user);
        return service.addNewUser(user);
    }

    @PatchMapping("/{userId}")
    public User patchUser(@RequestBody User user, @PathVariable Long userId) throws DuplicateEmailException {
        Optional<String> email = Optional.ofNullable(user.getEmail());
        if (email.isPresent()) {
            List<String> emails = service.getUsers().stream()
                    .map(User::getEmail).collect(Collectors.toList());
            if (emails.contains(email.get())) {
                throw new DuplicateEmailException("Duplicate email");
            }
        }
        user.setId(userId);
        log.info("patch {}, {}", userId, user);
        return service.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }
}