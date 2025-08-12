package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.util.List;

@Slf4j
@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        log.debug("создание пользователя({})", user);
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) "
                        + "VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        User thisUser = jdbcTemplate.queryForObject(
                "SELECT * "
                        + "FROM users "
                        + "WHERE email=?", new UserMapper(), user.getEmail());
        log.trace("{} полльзователь создан в БД", thisUser);
        return thisUser;
    }

    @Override
    public User updateUser(User user) {
        log.debug("обновление пользователя({})", user);
        jdbcTemplate.update("UPDATE users "
                        + "SET email=?, login=?, name=?, birthday=? "
                        + "WHERE id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        User thisUser = getById(user.getId());
        log.trace("Пользователь {} обновлен в БД", thisUser);
        return thisUser;
    }

    @Override
    public User getById(Long id) {
        log.debug("получить пользователя по id({})", id);
        User thisUser = jdbcTemplate.queryForObject(
                "SELECT * FROM users "
                        + "WHERE id=?", new UserMapper(), id);
        log.trace("Пользователь {} возвращен", thisUser);
        return thisUser;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("получить всех пользователей()");
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users ",
                new UserMapper());
        log.trace("Список пользователей возвращен из БД: {}", users);
        return users;
    }

    @Override
    public void deleteUser(Long id) {
        var delete = this.jdbcTemplate.update(
                "DELETE FROM users WHERE id = ?",
                id);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        return getById(userId);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        return getById(userId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return null;
    }

    @Override
    public boolean isContains(Long id) {
        try {
            getById(id);
            log.trace("Пользователь с id {} найден", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Не найден пользователь с id {}", id);
            return false;
        }
    }
}
