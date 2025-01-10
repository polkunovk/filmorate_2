package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendsIdsMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserDbStorage.class,
        UserRowMapper.class,
        FriendsIdsStorage.class,
        FriendsIdsMapper.class,
})
public class FriendsIdsStorageTest {
    private final FriendsIdsStorage idsStorage;
    private final UserDbStorage storage;

    @BeforeEach
    public void setup() {
        User user1 = new User();
        user1.setName("testName");
        user1.setLogin("testLogin");
        user1.setEmail("testMail");

        User user2 = new User();
        user2.setName("testName2");
        user2.setLogin("testLogin2");
        user2.setEmail("testMail2");

        storage.addUser(user1);
        storage.addUser(user2);
    }

    @Test
    public void shouldAddFriend() {
        int userId1 = storage.findAll().get(0).getId();
        int userId2 = storage.findAll().get(1).getId();

        assertThat(storage.findById(userId1)).isPresent();
        assertThat(storage.findById(userId2)).isPresent();

        idsStorage.addFriend(userId1, userId2);

        FriendsIds friend = idsStorage.findUserFriends(userId1).get(0);

        assertThat(friend).isNotNull();
    }

    @Test
    public void shouldFindAllFriends() {
        int firstUserId = storage.findAll().get(0).getId();
        int secondUserId = storage.findAll().get(1).getId();

        assertThat(storage.findById(firstUserId)).isPresent();
        assertThat(storage.findById(secondUserId)).isPresent();

        idsStorage.addFriend(firstUserId, secondUserId);
        idsStorage.addFriend(secondUserId, firstUserId);

        FriendsIds firstFriend = idsStorage.findAll().get(0);
        FriendsIds secondFriend = idsStorage.findAll().get(1);
        int totalFriends = idsStorage.findAll().size();

        assertThat(firstFriend).isNotNull();
        assertThat(secondFriend).isNotNull();
        assertThat(totalFriends).isEqualTo(2);
    }

    @Test
    public void shouldDeleteFriend() {
        int firstUserId = storage.findAll().get(0).getId();
        int secondUserId = storage.findAll().get(1).getId();

        assertThat(storage.findById(firstUserId)).isPresent();
        assertThat(storage.findById(secondUserId)).isPresent();

        idsStorage.addFriend(firstUserId, secondUserId);
        idsStorage.addFriend(secondUserId, firstUserId);

        idsStorage.deleteLFriend(secondUserId, firstUserId);

        int remainingFriends = idsStorage.findAll().size();

        assertThat(remainingFriends).isEqualTo(1);
    }
}
