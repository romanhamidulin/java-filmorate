package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaStorage implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(int id) {
        log.debug("рейтинг по id({})", id);
        Mpa mpa = jdbcTemplate.queryForObject("SELECT *FROM film_mpa WHERE id=?",
                new MpaMapper(), id);
        log.trace("рейтинг MPA по id {} возвращен", mpa);
        return mpa;
    }

    @Override
    public List<Mpa> getMpaList() {
        log.debug("список рейтингов()");
        List<Mpa> mpaList = jdbcTemplate.query("SELECT * FROM film_mpa ORDER BY id",
                new MpaMapper());
        log.trace("Весь список рейтингов: {}", mpaList);
        return mpaList;
    }

    @Override
    public boolean isContains(int id) {
        try {
            getMpaById(id);
            log.trace("Рейтинг с id {} найден", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Рейтинг с таким  {} не найден", id);
            return false;
        }
    }
}