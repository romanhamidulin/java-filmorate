package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getById(int id);

    void deleteFilm(int id);

    Film addLike(int id, int userId);

    Film removeLike(int id, int userId);

    Film addGenres(int id, Set<Genre> genres);

    Film updateGenres(int id, Set<Genre> genres);

    Set<Genre> getGenres(int id);

    Film deleteGenres(int id);

}
