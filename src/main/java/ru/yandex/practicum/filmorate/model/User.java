package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {

    private int id;

    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(message = "Электронная почта должна содержать '@' и быть валидной.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы.")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть пустой.")
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    private Map<Long, FriendshipStatus> friends = new HashMap<>();

    public String getName() {
        if (name == null || name.isBlank()) {
            return login;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addFriend(Long friendId, FriendshipStatus status) {
        friends.put(friendId, status);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public Map<Long, FriendshipStatus> getFriends() {
        return friends;
    }
}
