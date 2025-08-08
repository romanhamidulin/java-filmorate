package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getById(int id);

    void deleteUser(int id);

    User addFriend(int userId, int friendId);

    User removeFriend(int userId, int friendId);

    List<User> getFriends(int userId);
}
