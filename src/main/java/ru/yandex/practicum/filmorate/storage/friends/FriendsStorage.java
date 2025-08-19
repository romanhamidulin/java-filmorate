package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendsStorage implements FriendsDao {

    private static final String FIND_BY_ID_QUERY = "select u.* from user_friends f left join users u on f.friend_id = u.id where f.user_id = ?";
    private static final String FIND_COMMON_BY_ID_QUERY = "select count(*) from user_friends where user_id = ? and friend_id = ?";
    private static final String GET_COMMON_FRIENDS = "select u.* from (select * from user_friends where user_id = ?) f1 " +
            "join (select * from user_friends where user_id = ?) f2 on f1.friend_id = f2.friend_id join users u on f1.friend_id = u.id";
    private static final String INSERT_QUERY = "insert into user_friends(user_id, friend_id) values (?, ?)";
    private static final String DELETE_QUERY = "delete from user_friends where user_id = ? and friend_id = ?";
    private static final String UPDATE_FRIENDSHIP_QUERY = "update user_friends set isFriend = true " +
            "WHERE user_id = ? and friend_id = ? or friend_id = ? and user_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.debug("добавить друзей ({}, {}, {})", userId, friendId);
        jdbcTemplate.update(INSERT_QUERY, userId, friendId);
        if (isFriend(userId, friendId)) {
            jdbcTemplate.update(UPDATE_FRIENDSHIP_QUERY, userId, friendId, userId, friendId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.debug("удалить друзей ({}, {})", userId, friendId);
        jdbcTemplate.update(DELETE_QUERY, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        log.debug("получить список друзей({})", userId);
        return jdbcTemplate.query(FIND_BY_ID_QUERY, new UserMapper(), userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        return jdbcTemplate.query(GET_COMMON_FRIENDS, new UserMapper(), userId, friendId);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        return jdbcTemplate.queryForObject(FIND_COMMON_BY_ID_QUERY, Integer.class, friendId, userId) > 0;
    }
}
