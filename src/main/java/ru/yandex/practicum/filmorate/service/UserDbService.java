package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDbService {
    private final UserDbStorage userStorage;
    private final FriendService friendService;

    public User createUser(User user) {
        if (userStorage.isContains(user.getId())) {
            throw new ObjectAlreadyExistsException(format("Пользователь с таким id %d уже существует", user.getId()));
        }

        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.isContains(user.getId())) {
            throw new NotFoundException("Пользователь для обновления не найден");
        }
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getById(id).orElseThrow(() -> new NotFoundException("Фильм не найдем"));
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteUser(Long id) {
        if (!userStorage.isContains(id)) {
            throw new NotFoundException(format("Не найден пользователь с таким id %d", id));
        }
        log.info("Пользователь удален.");
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        if (!userStorage.isContains(userId) || !userStorage.isContains(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (userId.equals(friendId)) {
            throw new ObjectAlreadyExistsException("Пользователь не может добавить в друзья самого себя");
        }
        friendService.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (!userStorage.isContains(userId) || !userStorage.isContains(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        friendService.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long id) {
        if (!userStorage.isContains(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return friendService.getFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        if (!userStorage.isContains(userId) || !userStorage.isContains(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return friendService.getCommonFriends(userId, friendId);
    }

    private static void validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

}
