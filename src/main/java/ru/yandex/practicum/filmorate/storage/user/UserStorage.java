package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getById(Long id);

    void deleteUser(Long id);

    User addFriend(Long userId, Long friendId);

    User removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    boolean isContains(Long id);
}
