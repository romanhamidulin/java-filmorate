package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.mapper.LikeMapper;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeStorage implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addlike(Long filmId, Long userId) {
        log.debug("добавить лайк({}, {})", filmId, userId);
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        log.trace("Фильм {} лайкнут пользователем {}", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.debug("удалить лайк({}, {})", filmId, userId);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
        log.trace("Фильм {} дизлайкнут пользователем {}", userId, filmId);
    }

    @Override
    public int countLikes(Long filmId) {
        log.debug("количество лайков({}).", filmId);
        Integer count = Objects.requireNonNull(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id=?", Integer.class, filmId));
        log.trace("Фильм {} лайкнут {} раз", filmId, count);
        return count;
    }

    @Override
    public boolean isLiked(Long filmId, Long userId) {
        try {
            jdbcTemplate.queryForObject("SELECT film_id, user_id FROM likes WHERE film_id=? AND user_id=?",
                    new LikeMapper(), filmId, userId);
            log.trace("Фильм {} уже лайкнут пользователем {}", filmId, userId);
            return true;
        } catch (NotFoundException exception) {
            log.trace("Не найдено лайков  для фильма {} от пользователя {}", filmId, userId);
            return false;
        }
    }
}
