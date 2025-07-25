package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        List<Film> list = filmStorage.getAllFilms();
        log.info("Получен список всех фильмов.");
        return list;
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        Film savedFilm = filmStorage.addFilm(film);
        log.info("Фильм сохранен.");
        return savedFilm;
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Данные фильма обновлены.");
        return updatedFilm;
    }

    public Film getById(int id) {
        Film film = filmStorage.getById(id);
        log.info("Получен фильм с идентификатором " + id + ".");
        return film;
    }

    public void deleteFilm(int id) {
        log.info("Фильм удален.");
        filmStorage.deleteFilm(id);
    }

    public Film addLike(int filmId, int userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь чтобы поставит лайк не найден " + userId + " ");
        }
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
        log.info("Пользователь " + " поставил лайк фильму " + film.getName() + ".");
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк от пользователя " + userId + " не найден");
        }
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    public static void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String message = "Дата релиза не может быть нулевой или раньше " + MIN_RELEASE_DATE;
            log.error(message);
            throw new ValidationException(message);
        }

        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            String message = "Продолжительность должна быть положительной";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}
