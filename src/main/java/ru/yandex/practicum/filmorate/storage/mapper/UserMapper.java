package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserMapper {

    public static User mapToUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name == null || name.isBlank() ? login : name);
        user.setBirthday(birthday);
        user.setFriends(new HashMap<>());
        return user;
    }

    public static User updateUserFields(User user, Map<String, Object> updateData) {
        if (updateData.containsKey("email")) {
            user.setEmail((String) updateData.get("email"));
        }
        if (updateData.containsKey("login")) {
            user.setLogin((String) updateData.get("login"));
        }
        if (updateData.containsKey("name")) {
            String name = (String) updateData.get("name");
            user.setName(name == null || name.isBlank() ? user.getLogin() : name);
        }
        if (updateData.containsKey("birthday")) {
            user.setBirthday((LocalDate) updateData.get("birthday"));
        }
        return user;
    }

    public static Map<String, Object> mapToUserDto(User user) {
        Map<String, Object> userDto = new HashMap<>();
        userDto.put("id", user.getId());
        userDto.put("email", user.getEmail());
        userDto.put("login", user.getLogin());
        userDto.put("name", user.getName());
        userDto.put("birthday", user.getBirthday());
        userDto.put("friends", user.getFriends());
        return userDto;
    }
}
