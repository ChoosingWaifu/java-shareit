package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

public class UserMapperTest {


    private final UserDto userDto1 = new UserDto(null,"A","waifu@yandex.ru");

    private final UserDto userDto2 = new UserDto(null,"B","waifu2@yandex.ru");

    private final User user1 = new User(1L,"A","waifu@yandex.ru");

    private final User user2 = new User(2L,"B","waifu2@yandex.ru");

    @Test
    void toUserTest() {
        User test = UserMapper.toUser(userDto1);
        test.setId(user1.getId());
        Assertions.assertEquals(user1.toString(), test.toString());
    }

    @Test
    void toUserDtoTest() {
        UserDto test = UserMapper.toUserDto(user1);
        test.setId(null);
        Assertions.assertEquals(userDto1.toString(), test.toString());
    }

    @Test
    void toUserDtoListTest() {
        List<UserDto> result = UserMapper.toUserDtoList(List.of(user1, user2));
        userDto1.setId(1L);
        userDto2.setId(2L);
        Assertions.assertEquals(result.toString(), List.of(userDto1,userDto2).toString());
    }

}
