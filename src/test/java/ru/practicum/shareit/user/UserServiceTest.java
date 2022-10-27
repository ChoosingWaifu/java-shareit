package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository mockUserRepository;

    private final User user1 = new User(1L,"A","waifu@yandex.ru");
    private final User user1Update = new User(1L,"Update A","waifu@yandex.ru");
    private final UserDto userDto1 = new UserDto(1L,"A","waifu@yandex.ru");
    private final UserDto userDto1Update = new UserDto(1L,"Update A","waifu@yandex.ru");

    @Test
    public void addUser() {
        Mockito.when(mockUserRepository.save(any()))
               .thenReturn(user1);
        Assertions.assertEquals(userDto1.toString(), userService.addNewUser(userDto1).toString());
    }

    @Test
    public void updateUser() throws Exception {
        Mockito.when(mockUserRepository.findById(any()))
                .thenReturn(Optional.of(user1));
        Mockito.when(mockUserRepository.save(any()))
                .thenReturn(user1Update);
        Assertions.assertEquals(userDto1Update.toString(), userService.updateUser(userDto1Update).toString());
    }

    @Test
    public void getById() throws Exception {
        Mockito.when(mockUserRepository.findById(any()))
                .thenReturn(Optional.of(user1));
        Assertions.assertEquals(userDto1.toString(), userService.getById(user1.getId()).toString());
    }

    @Test
    public void getUsers() {
        Mockito.when(mockUserRepository.findAll())
                .thenReturn(List.of(user1));
        Assertions.assertEquals(List.of(userDto1).toString(), userService.getUsers().toString());
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(user1.getId());
        Mockito.verify(mockUserRepository, Mockito.times(1)).deleteById(user1.getId());
    }
}