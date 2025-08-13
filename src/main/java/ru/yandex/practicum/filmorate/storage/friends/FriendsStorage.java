package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.mapper.FriendshipMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendsStorage implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId, boolean isFriend) {
        log.debug("добавить друзей ({}, {}, {})", userId, friendId, isFriend);
        jdbcTemplate.update("INSERT INTO user_friends (user_id, friend_id, isFriend) VALUES(?, ?, ?)",
                userId, friendId, isFriend);
        Friendship friendship = getFriend(userId, friendId);
        log.trace("Список друзей: {}", friendship);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.debug("удалить друзей ({}, {})", userId, friendId);
        Friendship friendship = Objects.requireNonNull(getFriend(userId, friendId));
        if (friendship.isFriend()) {
            jdbcTemplate.update("UPDATE user_friends SET isFriend=false WHERE user_id=? AND friend_id=?",
                    userId, friendId);
            log.debug("Пользователи {} и {} больше не друзья", userId, friendId);
        }
        else {
            jdbcTemplate.update("DELETE FROM user_friends WHERE user_id=? AND friend_id=?", userId, friendId);
        }
        log.trace("Список без друзей: {}", friendship);
    }

    @Override
    public List<Long> getFriends(Long userId) {
        log.debug("получить список друзей({})", userId);
        List<Long> friendsList = jdbcTemplate.query(
                        "SELECT user_id, friend_id, isFriend FROM user_friends WHERE user_id=?",
                        new FriendshipMapper(), userId)
                .stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toList());
        log.trace("Список друзей пользователя {} : {}", userId, friendsList);
        return friendsList;
    }

    @Override
    public Friendship getFriend(Long userId, Long friendId) {
        return jdbcTemplate.queryForObject(
                "SELECT user_id, friend_id, isFriend FROM user_friends WHERE user_id=? AND friend_id=?",
                new FriendshipMapper(), userId, friendId);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        try {
            getFriend(userId, friendId);
            log.trace("Найдена дружба между {} и {}", userId, friendId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Не найдена дружба между {} и {}", userId, friendId);
            return false;
        }
    }
}
