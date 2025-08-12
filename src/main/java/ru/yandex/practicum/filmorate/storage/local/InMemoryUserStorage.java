package ru.yandex.practicum.filmorate.storage.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Long> friends= new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User createUser(User user) {
        log.info("Получен запрос на создание пользователя: {}", user);


        // Если имя не указано, используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, используется логин: {}", user.getLogin());
        }

        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Получен запрос на обновление пользователя с id {}: {}", user.getId(), user);

        if (!users.containsKey(user.getId())) {
            String message = "Пользователь с id " + user.getId() + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен: {}", user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            String message = "Пользователь с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            String message = "Пользователь с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        users.remove(id);
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        if (friends.containsKey(id)) {
            friends.put(id, friendId);
        } else {
            String message = "Неудачная попыдка добавить друзей пользователь с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return users.get(id);
    }

    @Override
    public User removeFriend(Long id, Long friendId) {
        if (friends.containsKey(id)) {
            friends.remove(id, friendId);
        } else {
            String message = "Неудачная попыдка удалить друзей пользователь с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return users.get(id);
    }

    @Override
    public List<User> getFriends(Long id) {
        List<User> friendsList = new ArrayList<>();
        if (friends.containsKey(id)) {
            for (Map.Entry<Long, Long> entry : friends.entrySet()) {
                Long uId = entry.getKey();
                if (uId.equals(id)) {
                    User friend = getById(entry.getValue());
                    friendsList.add(friend);
                }
            }
        }
        return friendsList;
    }

    @Override
    public boolean isContains(Long id) { return false; }
}
