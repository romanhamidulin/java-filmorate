package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "select f.*, m.name from films f left join " +
            "film_mpa m on f.mpa_id = m.id";
    private static final String FIND_BY_ID_QUERY = "select f.*, m.name as name from films f " +
            "left join film_mpa m on f.mpa_id = m.id where f.id = ?";
    private static final String INSERT_QUERY = "insert into films(name, description, release_date, duration, mpa_id) " +
            "values (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "update films set name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE id = ?";
    private static final String TOP_POPULAR_QUERY = "select f.*, m.name from films f " +
            "left join (select film_id, count(distinct user_id) as likes_count from film_likes group by film_id) l on f.id = l.film_id " +
            "left join film_mpa m on f.mpa_id = m.id order by likes_count desc limit ?";
    private static final String DELETE_GENRE_FILM_QUERY = "delete from film_genre where film_id = ?";
    private static final String INSERT_GENRE_FILM_QUERY = "insert into film_genre(film_id, genre_id) values(?, ?)";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("создаем фильм({})", film);
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId()
        );
        film.setId(id);
        updateGenres(film.getGenres(), film.getId());
        log.trace("Фильм {} добавлен в БД", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("обновляем фильм({}).", film);
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId(),
                film.getId()
        );
        updateGenres(film.getGenres(), film.getId());
        return film;
    }

    @Override
    public Optional<Film>  getById(Long id) {
        log.debug("получаем фильм по id({})", id);
        Optional<Film> thisFilm = findOne(FIND_BY_ID_QUERY, id);
        log.trace("Фильм {} возвращен", thisFilm);
        return thisFilm;
    }

    @Override
    public void deleteFilm(Long id) {
        delete("DELETE FROM films WHERE id = ?",
                id);
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("получаем все фильмы()");
        List<Film> films = findMany(FIND_ALL_QUERY);
        log.trace("Список всех фильмов в БД: {}", films);
        return films;
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        return findMany(TOP_POPULAR_QUERY, count);
    }

    public void updateGenres(Set<Genre> genres, Long filmId) {
        boolean isDeleted = delete(DELETE_GENRE_FILM_QUERY, filmId);
        if (!genres.isEmpty()) {
            List<Genre> genresList = genres.stream().toList();
            jdbc.batchUpdate(INSERT_GENRE_FILM_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, filmId);
                    ps.setInt(2, genresList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genresList.size();
                }
            });
        }
    }

    @Override
    public boolean isContains(Long id) {
        return getById(id).isPresent();
    }
}
