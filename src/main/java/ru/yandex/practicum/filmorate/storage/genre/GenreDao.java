package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreDao {

    Optional<Genre> getGenreById(int id);

    List<Genre> getAllGenres();

    Set<Genre> getGenresByFilmId(Long filmId);

}
