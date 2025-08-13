package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreDbService {
    private final GenreDao genreDao;

    public Genre getGenreById(Integer id) {
        if (id == null || !genreDao.isContains(id)) {
            throw new NotFoundException("Жанр не найден");
        }
        return genreDao.getGenreById(id);
    }

    public List<Genre> getGenres() {
        return genreDao.getGenres();
    }

}
