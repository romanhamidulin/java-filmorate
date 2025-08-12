package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
public class FilmDbService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmDbService(@Qualifier("FilmDbStorage") FilmDbStorage filmStorage,
                         @Qualifier("UserDbStorage") UserDbStorage userStorage,
                         GenreDao genreDao,
                         MpaDao mpaDao,
                         LikeDao likeDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likeDao = likeDao;
    }

    public Film addFilm(Film film) {
        checkIfExists(film);
        validateFilm(film);
        Film thisFilm = filmStorage.addFilm(film);
        filmStorage.addGenres(thisFilm.getId(), film.getGenres());
        thisFilm.setGenres(filmStorage.getGenres(thisFilm.getId()));
        thisFilm.setMpa(mpaDao.getMpaById(thisFilm.getMpa().getId()));
        return thisFilm;
    }

    public Film updateFilm(Film film) {
        checkIfNotExists(film);
        validateFilm(film);
        Film thisFilm = filmStorage.updateFilm(film);
        filmStorage.updateGenres(thisFilm.getId(), film.getGenres());
        thisFilm.setGenres(filmStorage.getGenres(thisFilm.getId()));
        thisFilm.setMpa(mpaDao.getMpaById(thisFilm.getMpa().getId()));
        return thisFilm;
    }

    public Film getById(Long filmId) {
        if (!filmStorage.isContains(filmId)) {
            throw new ObjectNotFoundException("Не найден фильм с таким id " + filmId);
        }
        Film film = filmStorage.getById(filmId);
        film.setGenres(filmStorage.getGenres(filmId));
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        return film;
    }

    public List<Film> getAllFilms() {
        var films = filmStorage.getAllFilms();
        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()));
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        }
        return films;
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularMovies = getAllFilms()
                .stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
        log.trace("Список популярных фильмов: {}", popularMovies);
        return popularMovies;
    }

    public void deleteFilm(Long id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        }
        log.info("Фильм удален.");
        filmStorage.deleteFilm(id);
    }

    public void addLike(Long filmId, Long userId) {
        likeChecker(filmId, userId);
        if (likeDao.isLiked(filmId, userId)) {
            String message = format("Пользователь с id %d уже лайкнул фильм %d", userId, filmId);
            log.error(message);
            throw new ObjectAlreadyExistsException(message);
        }
        likeDao.removeLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        likeChecker(filmId, userId);
        if (!likeDao.isLiked(filmId, userId)) {
            String message = format("Пользователь с id %d не лайкал фильм %d", userId, filmId);
            log.error(message);
            throw new ObjectAlreadyExistsException(message);
        }
        likeDao.removeLike(filmId, userId);
    }

    private void checkIfNotExists(Film film) {
        if (!filmStorage.isContains(film.getId())) {
                throw new ObjectNotFoundException(format("Фильм с идентификатором %d не был найден", film.getId()));
            }

        if (!mpaDao.isContains(film.getMpa().getId())) {
            throw new ObjectNotFoundException(format("Не найден MPA для фильма с идентификатором %d", film.getId()));
        }
        for (Genre genre : film.getGenres()) {
            if (!genreDao.isContains(genre.getId())) {
                throw new ObjectNotFoundException("Не удалось найти жанр фильма с идентификатором" + film.getId());
            }
        }
    }

    private void checkIfExists(Film film) {
       if (filmStorage.isContains(film.getId())) {
                throw new ObjectAlreadyExistsException(format("Фильм с идентификатором %d уже существует", film.getId()));

        }
        if (!mpaDao.isContains(film.getMpa().getId())) {
            throw new ObjectNotFoundException(format("Не найден MPA для фильма с идентификатором %d", film.getId()));
        }
        for (Genre genre : film.getGenres()) {
            if (!genreDao.isContains(genre.getId())) {
                throw new ObjectNotFoundException("Не удалось найти жанр фильма с идентификатором" + film.getId());
            }
        }
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
        /*if (film.getGenres() == null) {
            throw new ValidationException("Жанр фильма не может быть пустым");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("Рейтинг MPA не может быть пустым");
        }*/

        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            String message = "Продолжительность должна быть положительной";
            log.error(message);
            throw new ValidationException(message);
        }
    }

    private int compare(Film film, Film otherFilm) {
        return Integer.compare(likeDao.countLikes(otherFilm.getId()), likeDao.countLikes(film.getId()));
    }

    private void likeChecker(Long filmId, Long userId) {
        if (!filmStorage.isContains(filmId)) {
            String message = "Не найден фильм с id " + filmId;
            throw new ObjectNotFoundException(message);
        }
        if (!userStorage.isContains(userId)) {
            String message = "Не найден пользователь с id " + userId;
            throw new ObjectNotFoundException(format("Unable to find a user with id %d", userId));
        }
    }
}
