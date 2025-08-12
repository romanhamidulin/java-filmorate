package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {
    private final UserService userService;

    @Test
    void testSaveFilm() {

        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);

        assertEquals("validLogin", user.getLogin());
        assertEquals(1, user.getId());
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void testUpdateUser() {


        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);
        user.setBirthday(LocalDate.of(1988, 8, 15));
        user.setEmail("elena@yandex.ru");
        userService.updateUser(user);

        assertEquals("elena@yandex.ru", userService.getUserById(user.getId()).getEmail());
        assertEquals(LocalDate.of(1988, 8, 15), userService.getUserById(user.getId()).getBirthday());
    }

    @Test
    void testGetUserById() {


        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);


        User user1 = new User();
        user1.setEmail("1valid@example.com");
        user1.setLogin("1validLogin");
        user1.setName("1Valid Name");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user1);

        assertEquals("Valid Name", user.getName());
        assertEquals("1Valid Name", user1.getName());
    }

    @Test
    void testGetAllUsers() {


        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);

        User user1 = new User();
        user1.setEmail("1valid@example.com");
        user1.setLogin("1validLogin");
        user1.setName("1Valid Name");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user1);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void testSaveFriend() {


        User user = new User();
        user.setEmail("1valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);


        User user1 = new User();
        user1.setEmail("valid@example.com");
        user1.setLogin("validLogin");
        user1.setName("Valid Name");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user1);

        userService.addFriend(1L, 2L);

        List<User> friends = userService.getFriends(user1.getId());

        assertEquals(friends.size(), 1);
    }

    @Test
    void testRemoveFriend() {


        User user = new User();
        user.setEmail("1valid@example.com");
        user.setLogin("1validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);


        User user2 = new User();
        user2.setEmail("valid@example.com");
        user2.setLogin("validLogin");
        user2.setName("Valid Name");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user2);

        userService.addFriend(1L, 2L);

        userService.removeFriend(1L, 2L);

        List<User> friends = userService.getFriends(user.getId());

        assertEquals(friends.size(), 0);
    }

    @Test
    void testGetFriends() {

        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);


        User user2 = new User();
        user2.setEmail("2valid@example.com");
        user2.setLogin("2validLogin");
        user2.setName("2Valid Name");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user2);

        userService.addFriend(1L, 2L);

        List<User> friends = userService.getFriends(user.getId());

        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), user2);
    }


}