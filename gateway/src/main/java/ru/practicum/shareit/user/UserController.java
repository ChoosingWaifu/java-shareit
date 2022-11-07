package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserValidateDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> get() {
        log.info("gateway get users");
        return client.get();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("gateway get user by id {}", userId);
        return client.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserValidateDto userDto) {
        log.info("gateway created new user {}", userDto);
        return client.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUser(@RequestBody UserValidateDto userDto, @PathVariable Long userId) {
        log.info("gateway patch {}, {}", userId, userDto);
        return client.patchUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("gateway delete {}", userId);
        return client.deleteUser(userId);
    }
}