package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.sql.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String DELETE_GENRE_FILM_QUERY = "delete from film_genre where film_id = ?";
    private static final String INSERT_GENRE_FILM_QUERY = "insert into film_genre (film_id, genre_id) values(?, ?)";

    @Override
    public Film addFilm(Film film) {
        log.debug("создаем фильм({})", film);
        jdbcTemplate.update(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId());
        Film thisFilm = jdbcTemplate.queryForObject(
                "SELECT * FROM films WHERE name=? "
                        + "AND description=? AND release_date=? AND duration=? AND mpa_id=?",
                new FilmMapper(), film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId());
        log.trace("Фильм {} добавлен в БД", thisFilm);
        return thisFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("обновляем фильм({}).", film);
        jdbcTemplate.update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId(),
                film.getId());
        Film thisFilm = getById(film.getId());
        log.trace("Фильм {} обновлен в БД", thisFilm);
        return thisFilm;
    }

    @Override
    public Film getById(Long id) {
        log.debug("получаем фильм по id({})", id);
        Film thisFilm = jdbcTemplate.queryForObject(
                FIND_BY_ID_QUERY,
                new FilmMapper(), id);
        log.trace("Фильм {} возвращен", thisFilm);
        return thisFilm;
    }

    @Override
    public void deleteFilm(Long id) {
        var delete = this.jdbcTemplate.update(
                "DELETE FROM films WHERE id = ?",
                id);
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("получаем все фильмы()");
        List<Film> films = jdbcTemplate.query(
                FIND_ALL_QUERY, new FilmMapper());
        log.trace("Список всех фильмов в БД: {}", films);
        return films;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        return getById(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        return getById(filmId);
    }

    @Override
    public Film addGenres(Long filmId, Set<Genre> genres) {
        log.debug("Добавить жанры({}, {})", filmId, genres);
        for (Genre genre : genres) {
            jdbcTemplate.update(INSERT_GENRE_FILM_QUERY, filmId, genre.getId());
            log.trace("Жанры добавили к фидьму {}", filmId);
        }
        return getById(filmId);
    }

    @Override
    public Film updateGenres(Long filmId, Set<Genre> genres) {
        log.debug("обновить жанры({}, {})", filmId, genres);
        deleteGenres(filmId);
        addGenres(filmId, genres);
        return getById(filmId);
    }

    @Override
    public LinkedHashSet<Genre> getGenres(Long filmId) {
        log.debug("получить жанры({})", filmId);
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(jdbcTemplate.query(
                "SELECT f.genre_id as id, g.name as name FROM film_genre AS f LEFT OUTER JOIN genre AS g ON f.genre_id = g.id WHERE f.film_id=? ORDER BY g.id",
                new GenreMapper(), filmId));
        log.trace("Жанры для фильма {} возвращены", filmId);
        return genres;
    }

    @Override
    public Film deleteGenres(Long filmId) {
        log.debug("удаляем жанры({})", filmId);
        jdbcTemplate.update(DELETE_GENRE_FILM_QUERY, filmId);
        log.trace("Удалены все жанры для фильма {}", filmId);
        return getById(filmId);
    }

    @Override
    public boolean isContains(Long id) {
        try {
            getById(id);
            log.trace("Такой фильм найден {}", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Не найдено фильмов в таким id {}", id);
            return false;
        }
    }
}
