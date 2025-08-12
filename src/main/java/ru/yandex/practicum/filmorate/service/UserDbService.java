package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDao;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
public class UserDbService {
    private final UserStorage userStorage;
    private final FriendsDao friendshipDao;

    @Autowired
    public UserDbService(@Qualifier("UserDbStorage") UserDbStorage userStorage,
                         FriendsDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
    }

    public User createUser(User user) {
        var test = user.getId();
        if (userStorage.isContains(user.getId())) {
            throw new ObjectAlreadyExistsException(format("Пользователь с таким id %d уже существует", user.getId()));
        }

        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.isContains(user.getId())) {
            throw new ObjectNotFoundException("Пользователь для обновления не найден");
        }
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        if (!userStorage.isContains(id)) {
            throw new ObjectNotFoundException(format("Не найден пользователь с таким id %d", id));
        }
        return userStorage.getById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteUser(Long id) {
        if (!userStorage.isContains(id)) {
            throw new ObjectNotFoundException(format("Не найден пользователь с таким id %d", id));
        }
        log.info("Пользователь удален.");
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        checkIfFriend(userId, friendId);
        boolean isFriend = friendshipDao.isFriend(userId, friendId);
        friendshipDao.addFriend(userId, friendId, isFriend);
    }

    public void deleteFriend(Long userId, Long friendId) {
        checkIfNotFriend(userId, friendId);
        friendshipDao.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long id) {
        if (!userStorage.isContains(id)) {
            throw new ObjectNotFoundException(format("Не найден пользователь с таким id %d", id));
        }
        List<User> friends = friendshipDao.getFriends(id).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(userStorage::getById)
                .collect(Collectors.toList());
        log.trace("Возвратили список друзей: {}", friends);
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        if (!userStorage.isContains(userId)) {
            String message = format("Не найден пользователь с таким id %d", userId);
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (!userStorage.isContains(friendId)) {
            String message = format("Не найден пользователь с таким id %d", friendId);
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (userId == friendId) {
            throw new ObjectNotFoundException("Невозможно получить список друзей " + userId);
        }
        List<User> userFriends = getFriends(userId);
        List<User> friendFriends = getFriends(friendId);
        return friendFriends.stream()
                .filter(userFriends::contains)
                .filter(friendFriends::contains)
                .collect(Collectors.toList());
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

    private void checkIfFriend(Long userId, Long friendId) {
        if (!userStorage.isContains(userId)) {
            String message = format("Не найден пользователь с таким id %d", userId);
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (!userStorage.isContains(friendId)) {
            String message = format("Не найден пользователь с таким id %d", friendId);
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (userId == friendId) {
            String message = format("Невозможно добавить в друзья самого себя", userId);
            log.error(message);
            throw new ObjectAlreadyExistsException(message);
        }
        if (friendshipDao.isFriend(userId, friendId)) {
            String message = format("Пользователь %d и %d уже дружат", userId, friendId);
            log.error(message);
            throw new ValidationException(message);
        }
    }
    private void checkIfNotFriend(Long userId, Long friendId) {
        if (!userStorage.isContains(userId)) {
            String message = format("Не найден пользователь с таким id %d", userId);
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (!userStorage.isContains(friendId)) {
            String message = format("Не найден пользователь с таким id %d", friendId);
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (userId == friendId) {
            String message = format("Невозможно добавить в друзья самого себя", userId);
            log.error(message);
            throw new ObjectAlreadyExistsException(message);
        }
        if (!friendshipDao.isFriend(userId, friendId)) {
            String message = format("Пользователь %d и %d не дружат", userId, friendId);
            log.error(message);
            throw new ValidationException(message);
        }
    }
}
