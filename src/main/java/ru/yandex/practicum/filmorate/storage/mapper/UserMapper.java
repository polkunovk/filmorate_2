package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToUser(NewUserRequest request) {
        return new User() {{
            setLogin(request.getLogin());
            setName(request.getName());
            setEmail(request.getEmail());
            setBirthday(request.getBirthday());
        }};
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto() {{
            setId(user.getId());
            setLogin(user.getLogin());
            setName(user.getName());
            setEmail(user.getEmail());
            setBirthday(user.getBirthday());
            setFriendsId(user.getFriendsId());
        }};
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasLogin()) user.setLogin(request.getLogin());
        if (request.hasName()) user.setName(request.getName());
        if (request.hasBirthday()) user.setBirthday(request.getBirthday());
        if (request.hasEmail()) user.setEmail(request.getEmail());

        return user;
    }
}
