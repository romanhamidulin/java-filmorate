package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreStorage implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        log.debug("жанр по id({})", id);
        Genre genre = jdbcTemplate.queryForObject("SELECT * FROM genre WHERE id=?",
                new GenreMapper(), id);
        log.trace("Жанс с id {} возвращен", id);
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        log.debug("все жанры()");
        List<Genre> genreList = jdbcTemplate.query("SELECT * FROM genre ORDER BY id",
                new GenreMapper());
        log.trace("Возвращены все жанры: {}", genreList);
        return genreList;
    }

    @Override
    public boolean isContains(int id) {
        try {
            getGenreById(id);
            log.trace("Жанр с id {} найден", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Жанр с таким  {} не найден", id);
            return false;
        }
    }
}
