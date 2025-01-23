package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userDbStorage;
    private final FriendsIdsStorage friendsIdsStorage;

    public UserDto addUser(NewUserRequest request) {
        validateUserRequest(request);
        User user = UserMapper.mapToUser(request);
        user = userDbStorage.addUser(user);
        log.info("Создан новый пользователь c id: {}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User user = validateUserExistenceAndReturn(request.getId());
        validateUniqueFields(request);
        User updatedUser = UserMapper.updateUserFields(user, request);
        updatedUser = userDbStorage.updateUser(updatedUser);
        log.info("Пользователь c id: {} обновлен", request.getId());
        return UserMapper.mapToUserDto(updatedUser);
    }

    public List<UserDto> getUsers() {
        log.info("Получен список пользователей.");
        return userDbStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Integer userId) {
        return UserMapper.mapToUserDto(userDbStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    public UserDto getUserByLogin(String login) {
        return UserMapper.mapToUserDto(userDbStorage.findByLogin(login).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    public UserDto getUserByEmail(String email) {
        return UserMapper.mapToUserDto(userDbStorage.findByEmail(email).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    public UserDto addFriend(Integer userId, Integer friendId) {
        validateUserExistence(userId, friendId);
        friendsIdsStorage.addFriend(userId, friendId);
        log.trace("Пользователь c id: {} добавил в друзья пользователя с id: {}", userId, friendId);
        return getUserById(userId);
    }

    public UserDto deleteFriend(Integer userId, Integer friendId) {
        validateUserExistence(userId, friendId);
        List<Integer> friendsIds = friendsIdsStorage.findUserFriends(userId)
                .stream()
                .map(FriendsIds::getFriendId)
                .collect(Collectors.toList());
        if (friendsIds.contains(friendId)) {
            friendsIdsStorage.deleteLFriend(friendId, userId);
            log.trace("Пользователь id: {} удален из друзей у пользователя с id: {}", friendId, userId);
        } else {
            log.trace("Пользователь с id: {} не был другом пользователя с id: {}", friendId, userId);
        }
        return getUserById(userId);
    }

    public List<UserDto> getFriends(Integer userId) {
        validateUserExistence(userId);
        List<Integer> friendsIds = friendsIdsStorage.findUserFriends(userId)
                .stream()
                .map(FriendsIds::getFriendId)
                .collect(Collectors.toList());
        return friendsIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<UserDto> commonFriends(Integer userId, Integer otherId) {
        validateUserExistence(userId, otherId);
        Set<UserDto> userFriends = new HashSet<>(getFriends(userId));
        Set<UserDto> otherUserFriends = new HashSet<>(getFriends(otherId));
        userFriends.retainAll(otherUserFriends);
        log.info("Список общих друзей пользователей с id: {} и {} сформирован.", userId, otherId);
        return new ArrayList<>(userFriends);
    }

    private void validateUserRequest(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        }
        if (userDbStorage.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("Электронная почта уже используется");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ConditionsNotMetException("Имя должно быть указано");
        }
        if (userDbStorage.findByLogin(request.getLogin()).isPresent()) {
            throw new ValidationException("Такой логин уже существует.");
        }
    }

    private void validateUniqueFields(UpdateUserRequest request) {
        if (request.getLogin() != null && userDbStorage.findByLogin(request.getLogin()).isPresent()) {
            throw new ValidationException("Такой логин уже существует.");
        }
        if (request.getEmail() != null && userDbStorage.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("Электронная почта уже используется");
        }
    }

    private void validateUserExistence(Integer... userIds) {
        for (Integer id : userIds) {
            if (userDbStorage.findById(id).isEmpty()) {
                log.error("Пользователь с id: {} не найден.", id);
                throw new NotFoundException("Пользователь с id: " + id + " не найден");
            }
        }
    }

    private User validateUserExistenceAndReturn(Integer userId) {
        return userDbStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }
}
