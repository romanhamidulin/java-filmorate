package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

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
    public User getById(int id) {
        if (!users.containsKey(id)) {
            String message = "Пользователь с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return users.get(id);
    }

    @Override
    public void deleteUser(int id) {
        if (!users.containsKey(id)) {
            String message = "Пользователь с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        users.remove(id);
    }
}
