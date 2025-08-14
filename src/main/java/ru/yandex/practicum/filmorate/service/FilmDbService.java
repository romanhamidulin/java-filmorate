package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmDbService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final LikeService likeService;
    private final MpaDbService mpaService;
    private final GenreDbService genreService;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);



    public Film addFilm(Film film) {
        validateFilm(film);
        if (film.getMpa() != null) {
            mpaService.getMpaById(film.getMpa().getId());
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre: film.getGenres()) {
                genreService.getGenreById(genre.getId());
            }
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        if (!filmStorage.isContains(film.getId())) {
            throw new NotFoundException("Фильм не найдем");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getById(Long filmId) {
        Film film = filmStorage.getById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найдем"));
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setGenres(genreService.getGenresByFilmId(film.getId()));
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getTopPopularFilms(count);
    }

    public void deleteFilm(Long id) {
        Optional<Film> film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        log.info("Фильм удален.");
        filmStorage.deleteFilm(id);
    }

    public void addLike(Long filmId, Long userId) {
        if (!filmStorage.isContains(filmId)) {
            throw new NotFoundException("Фильм не найден");
        }
        if (!userStorage.isContains(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        likeService.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!filmStorage.isContains(filmId)) {
            throw new NotFoundException("Фильм не найден");
        }
        if (!userStorage.isContains(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        likeService.removeLike(filmId, userId);
    }



    private void validateFilm(Film film) {
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
        if (film.getGenres() == null) {
            throw new ValidationException("Жанр фильма не может быть пустым");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("Рейтинг MPA не может быть пустым");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            String message = "Продолжительность должна быть положительной";
            log.error(message);
            throw new ValidationException(message);
        }
    }

}
