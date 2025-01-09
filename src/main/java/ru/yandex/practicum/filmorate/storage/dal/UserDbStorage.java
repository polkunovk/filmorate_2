package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Repository
public class UserDbStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @Override
    public User addUser(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NoSuchElementException("Пользователь с id " + user.getId() + " не найден.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден.");
        }
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден.");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        List<User> result = new ArrayList<>();
        for (Long id : ids) {
            if (users.containsKey(id.intValue())) {
                result.add(users.get(id.intValue()));
            }
        }
        return result;
    }

    @Override
    public List<User> getCommonFriends(Set<Long> userFriends, Set<Long> otherUserFriends) {
        Set<Long> commonIds = new HashSet<>(userFriends);
        commonIds.retainAll(otherUserFriends);
        return getUsersByIds(commonIds);
    }

    @Override
    public boolean userExists(Long userId) {
        return users.containsKey(userId.intValue());
    }
}
