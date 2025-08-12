package ru.yandex.practicum.filmorate.storage.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Slf4j
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Long> likes = new HashMap<>();
    private Long idCounter = 1L;


    @Override
    public Film addFilm(Film film) {
        log.info("Получен запрос на добавление фильма: {}", film);

        film.setId(idCounter++);
        films.put(film.getId(), film);

        log.info("Фильм создан: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Получен запрос на обновление фильма: {}", film);

        if (!films.containsKey(film.getId())) {
            String message = "Фильм с id " + film.getId() + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Получен запрос на получение списка всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Long id) {
        log.info("Получен запрос на получение фильма по id");
        if (!films.containsKey(id)) {
            String message = "Фильм с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return films.get(id);
    }

    @Override
    public Film addLike(Long id, Long userId) {
        if (likes.containsKey(id)) {
            likes.put(id, userId);
        } else {
            String message = "Невозможно добавить лайк фильму " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return films.get(id);
    }

    @Override
    public Film removeLike(Long id, Long userId) {
        if (likes.containsKey(id)) {
            likes.remove(id, userId);
        } else {
            String message = "Невозможно удалить лайк пользователя" +userId +"у фильма " + id ;
            log.error(message);
            throw new NotFoundException(message);
        }
        return films.get(id);
    }


    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Film addGenres(Long id, Set<Genre> genres) {
        return films.get(id);
    }

    @Override
    public Film updateGenres(Long id, Set<Genre> genres) {
        return films.get(id);
    }

    @Override
    public Set<Genre> getGenres(Long id) {
        return null;
    }

    @Override
    public Film deleteGenres(Long id) {
        return films.get(id);
    }

    @Override
    public boolean isContains(Long id) { return false; }
}