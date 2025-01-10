package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@Slf4j
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM \"USER\"";
    private static final String GET_USER_BY_ID_QUERY = "SELECT * FROM \"USER\" WHERE USER_ID = ?";
    private static final String ADD_USER_QUERY = "INSERT INTO \"USER\"(NAME, LOGIN, EMAIL, BIRTHDAY) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE \"USER\" SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";
    private static final String GET_USER_BY_EMAIL_QUERY = "SELECT * FROM \"USER\" WHERE EMAIL = ?";
    private static final String GET_USER_BY_LOGIN_QUERY = "SELECT * FROM \"USER\" WHERE LOGIN = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> findById(int id) {
        return findOne(GET_USER_BY_ID_QUERY, id);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(GET_USER_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findByLogin(String login) {
        return findOne(GET_USER_BY_LOGIN_QUERY, login);
    }

    public List<User> findAll() {
        return findMany(GET_ALL_USERS_QUERY);
    }

    public User addUser(User user) {
        if (user.getLogin() == null) {
            String generatedLogin = generateLogin(user);
            user.setLogin(generatedLogin);
            log.warn("Логин не передан, он будет сгенерирован автоматически.");
        }

        int id = insert(ADD_USER_QUERY, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday());
        user.setId(id);
        log.info("Создан новый пользователь с id: {}", user.getId());

        return user;
    }

    public User updateUser(User user) {
        if (user.getLogin() == null) {
            String generatedLogin = generateLogin(user);
            user.setLogin(generatedLogin);
            log.warn("Логин не передан, он будет сгенерирован автоматически.");
        }

        update(UPDATE_USER_QUERY, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        log.info("Пользователь с id: {} обновлен", user.getId());

        return user;
    }

    private String generateLogin(User user) {
        return user.getName() + "-" + user.getEmail();
    }
}
