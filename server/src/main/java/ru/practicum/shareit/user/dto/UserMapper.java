package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static User toUser(UserDto userDto) {
        return new User(
                null,
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<UserDto> toUserDtoList(List<User> userList) {
        List<UserDto> result = new ArrayList<>();
        for (User user: userList) {
            result.add(UserMapper.toUserDto(user));
        }
        return result;
    }

}
