package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getById(Long id);

    void deleteUser(Long id);

    boolean isContains(Long id);
}
