package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "select * from users";
    private static final String FIND_BY_ID_QUERY = "select * from users where id = ?";
    private static final String DELETE_BY_ID_QUERY = "delete from users where id = ?";
    private static final String INSERT_QUERY = "insert into users(name, email, login, birthday) values (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "update users set name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }
    @Override
    public User createUser(User user) {
        log.debug("создание пользователя({})", user);
        long id = insert(
                INSERT_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        log.trace("{} полльзователь создан в БД", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("обновление пользователя({})", user);
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        );
        log.trace("Пользователь {} обновлен в БД", user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        log.debug("получить пользователя по id({})", id);
        Optional<User> thisUser = findOne(FIND_BY_ID_QUERY, id);
        log.trace("Пользователь {} возвращен", thisUser);
        return thisUser;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("получить всех пользователей()");
        List<User> users =  findMany(FIND_ALL_QUERY);
        log.trace("Список пользователей возвращен из БД: {}", users);
        return users;
    }

    @Override
    public void deleteUser(Long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public boolean isContains(Long id) {
        return getById(id).isPresent();
    }
}
