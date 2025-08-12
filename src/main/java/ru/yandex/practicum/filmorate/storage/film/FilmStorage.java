package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getById(Long id);

    void deleteFilm(Long id);

    Film addLike(Long id, Long userId);

    Film removeLike(Long id, Long userId);

    Film addGenres(Long id, Set<Genre> genres);

    Film updateGenres(Long id, Set<Genre> genres);

    Set<Genre> getGenres(Long id);

    Film deleteGenres(Long id);

    boolean isContains(Long id);
}
