package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage extends IdGenerator implements UserStorage {
    private final Map<Integer, User> usersMap = new HashMap<>();

    public Map<Integer, User> getUsersMap() {
        return usersMap;
    }

    public Collection<User> getUsers() {
        log.info("Получен список пользователей.");
        return usersMap.values();
    }

    public Collection<Integer> getUsersIds() {
        log.info("Получен список id пользователей.");
        return usersMap.keySet();
    }

    public User addUser(User user) {
        if (isEmailTaken(user.getEmail())) {
            throw new ValidationException("Электронная почта уже используется.");
        }

        if (isLoginTaken(user.getLogin())) {
            throw new ValidationException("Такой логин уже существует.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Имя не передано, его заменит логин пользователя.");
        }

        if (user.getId() == null) {
            user.setId(generateNextUserId(usersMap));
        }

        usersMap.put(user.getId(), user);
        log.info("Создан новый пользователь c id: {}", user.getId());

        return user;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("Id не куказан.");
            throw new ValidationException("Id должен быть указан");
        }
        if (usersMap.containsKey(newUser.getId())) {
            if (isEmailTaken(newUser.getEmail())) {
                throw new ValidationException("Электронная почта уже используется");
            }
            if (isLoginTaken(newUser.getLogin())) {
                throw new ValidationException("Такой логин уже существует.");
            }

            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
                log.warn("Имя не передано, его заменит логин пользователя.");
            }

            User oldUser = usersMap.get(newUser.getId());

            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            if (!newUser.getLogin().equals(oldUser.getLogin())) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (!newUser.getEmail().equals(oldUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
            }
            log.info("Пользователь c id: {} обновлен", oldUser.getId());

            return oldUser;
        }
        log.error("User с id = {} не найден", newUser.getId());
        throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
    }

    private boolean isEmailTaken(String email) {
        return usersMap.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isLoginTaken(String login) {
        return usersMap.values().stream().anyMatch(user -> user.getLogin().equals(login));
    }

    private Integer generateNextUserId(Map<Integer, User> userMap) {
        return userMap.isEmpty() ? 1 : userMap.keySet().stream().max(Integer::compare).orElse(0) + 1;
    }
}
