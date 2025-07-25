package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;


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
    public Film getById(int id) {
        log.info("Получен запрос на получение фильма по id");
        if (!films.containsKey(id)) {
            String message = "Фильм с id " + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return films.get(id);
    }

    @Override
    public void deleteFilm(int id) {
        films.remove(id);
    }

}