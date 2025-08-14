package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class GenreStorage extends BaseStorage<Genre> implements GenreDao {

    private static final String FIND_ALL_QUERY = "select * from genre order by id";
    private static final String FIND_BY_ID_QUERY = "select * from genre where id = ? order by id";
    private static final String FIND_FILM_GENRES_BY_ID_QUERY = "SELECT f.genre_id as id, g.name as name FROM film_genre AS f LEFT OUTER JOIN genre " +
            "AS g ON f.genre_id = g.id WHERE f.film_id=? ORDER BY g.id";

    public GenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        log.debug("жанр по id({})", id);
        Optional<Genre> genre = findOne(FIND_BY_ID_QUERY, id);;
        log.trace("Жанс с id {} возвращен", id);
        return genre;
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("все жанры()");
        List<Genre> genreList = findMany(FIND_ALL_QUERY);;
        log.trace("Возвращены все жанры: {}", genreList);
        return genreList;
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {
        return new LinkedHashSet<>(findMany(FIND_FILM_GENRES_BY_ID_QUERY, filmId));
    }


}
