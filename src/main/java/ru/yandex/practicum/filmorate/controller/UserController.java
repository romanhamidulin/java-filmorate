package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody int userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Запрос на получение пользователя по id: {}", id);
        User user = userService.getUserById(id);
        log.info("Вернули пользователя: {}", user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на добавление друга: userId={}, friendId={}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Друг успешно добавлен");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на удаление друга: userId={}, friendId={}", id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Друг успешно удален");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Запрос на получение списка друзей: {}", id);
        List<User> friends = userService.getFriends(id);
        log.info("Удалили друзей {} У пользователя {}", friends.size(), id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable int id,
            @PathVariable int otherId) {
        log.info("Запрос на получение текущих друзей: {} and {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Найдены друзья {}", commonFriends.size());
        return commonFriends;
    }
}
