package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;

import java.time.LocalDate;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final UserDbService userService;
    private final JdbcTemplate jdbcTemplate;

    private final User user = new User();
    private final User updatedUser = new User();
    private final User friend  = new User();
    private final User friendOfBoth = new User();

    @BeforeEach
    void beforeEach() {
        user.setLogin("test");
        user.setName("testName");
        user.setEmail("test@ya.ru");
        user.setBirthday(LocalDate.now().minusYears(2));

        updatedUser.setLogin("test1");
        updatedUser.setName("testName1");
        updatedUser.setEmail("test@ya.ru");
        updatedUser.setBirthday(LocalDate.now().minusYears(3));

        friend.setLogin("test2");
        friend.setName("testName2");
        friend.setEmail("test2@ya.ru");
        friend.setBirthday(LocalDate.now().minusYears(4));

        friendOfBoth.setLogin("test3");
        friendOfBoth.setName("test3Name");
        friendOfBoth.setEmail("test3@ya.ru");
        friendOfBoth.setBirthday(LocalDate.now().minusYears(1));

        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM films");
    }

    @Test
    public void createUser() {
        userService.createUser(user);

        Assertions.assertFalse(userService.getAllUsers().isEmpty());
    }

    @Test
    public void createUserWithSameId() {
        userService.createUser(updatedUser);
        Assertions.assertThrows(DuplicateKeyException.class, () -> userService.createUser(updatedUser));
    }

    @Test
    public void updateUser() {
        User thisUser = userService.createUser(user);
        User thisUpdatedUser = userService.updateUser(thisUser);

        Assertions.assertEquals(thisUser.getEmail(), thisUpdatedUser.getEmail());
    }

    @Test
    public void updateNotExistUser() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    public void getUserById() {
        User newUser = userService.createUser(user);
        User thisUser = userService.getUserById(newUser.getId());

        Assertions.assertEquals(newUser.getEmail(), thisUser.getEmail());
    }

    @Test
    public void getUserByIfNotExistsId() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void addFriend() {
        User thisUser = userService.createUser(user);
        User thisFriend = userService.createUser(friend);
        userService.addFriend(thisUser.getId(), thisFriend.getId());
        userService.addFriend(thisFriend.getId(), thisUser.getId());

        Assertions.assertFalse(userService.getFriends(thisUser.getId()).isEmpty());
        Assertions.assertFalse(userService.getFriends(thisFriend.getId()).isEmpty());
    }

    @Test
    public void addFriend_shouldStayAsFollower() {
        User thisUser = userService.createUser(user);
        User thisFriend = userService.createUser(friend);
        userService.addFriend(thisUser.getId(), thisFriend.getId());

        Assertions.assertFalse(userService.getFriends(thisUser.getId()).isEmpty());
        Assertions.assertTrue(userService.getFriends(thisFriend.getId()).isEmpty());
    }

    @Test
    public void addFriendYourSelf() {
        User thisUser = userService.createUser(user);
        Assertions.assertThrows(ObjectAlreadyExistsException.class,
                () -> userService.addFriend(thisUser.getId(), thisUser.getId()));
    }

    @Test
    public void deleteFriend() {
        User thisUser = userService.createUser(user);
        User thisFriend = userService.createUser(friend);
        userService.addFriend(thisUser.getId(), thisFriend.getId());
        userService.addFriend(thisFriend.getId(), thisUser.getId());
        userService.deleteFriend(thisUser.getId(), thisFriend.getId());
        userService.deleteFriend(thisFriend.getId(), thisUser.getId());

        Assertions.assertTrue(userService.getFriends(thisUser.getId()).isEmpty());
        Assertions.assertTrue(userService.getFriends(thisFriend.getId()).isEmpty());
    }

    @Test
    public void deleteIfNotFriend() {
        User thisUser = userService.createUser(user);
        User thisFriend = userService.createUser(friend);

        Assertions.assertThrows(NoContentException.class,
                () -> userService.deleteFriend(thisUser.getId(), thisFriend.getId()));
        Assertions.assertThrows(NoContentException.class,
                () -> userService.deleteFriend(thisFriend.getId(), thisUser.getId()));
    }

    @Test
    public void getFriendsList() {
        User thisUser = userService.createUser(user);
        User thisFriend = userService.createUser(friend);
        userService.addFriend(thisUser.getId(), thisFriend.getId());
        userService.addFriend(thisFriend.getId(), thisUser.getId());

        Assertions.assertEquals(1, userService.getFriends(thisUser.getId()).size());
        Assertions.assertEquals(1, userService.getFriends(thisFriend.getId()).size());
    }

        @Test
    public void getCommonFriends() {
        User thisUser = userService.createUser(user);
        User thisFriend = userService.createUser(friend);
        userService.addFriend(thisUser.getId(), thisFriend.getId());
        userService.addFriend(thisFriend.getId(), thisUser.getId());

        Assertions.assertTrue(userService.getCommonFriends(thisUser.getId(), thisFriend.getId()).isEmpty());
        Assertions.assertTrue(userService.getCommonFriends(thisFriend.getId(), thisUser.getId()).isEmpty());
    }
}