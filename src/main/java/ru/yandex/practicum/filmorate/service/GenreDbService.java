package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreDbService {
    private final GenreStorage genreStorage;

    public Genre getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId).orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Set<Genre> getGenresByFilmId(Long filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }

}
