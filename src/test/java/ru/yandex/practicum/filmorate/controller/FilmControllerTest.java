package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmDbService;
import ru.yandex.practicum.filmorate.service.UserDbService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private final FilmDbService filmService;
    private final UserDbService userService;
    private final JdbcTemplate jdbcTemplate;
    private final User user = new User();;
    private final Film film = new Film();
    private final Film updatedFilm = new Film();
    private final Film oneMoreFilm = new Film();
    private final Film unexistingFilm =  new Film();
    private final Film popularFilm =  new Film();

    @BeforeEach
    void beforeEach() {
        user.setLogin("user");
        user.setName("userName");
        user.setBirthday(LocalDate.now());
        user.setEmail("user@ya.ru");

        film.setName("Фильм 1");
        film.setDescription("a".repeat(20));
        film.setReleaseDate(LocalDate.now().minusYears(10));
        film.setDuration(Duration.ofMinutes(100));

        updatedFilm.setName("Фильм 1");
        updatedFilm.setDescription("b".repeat(20));
        updatedFilm.setReleaseDate(LocalDate.now().minusYears(10));
        updatedFilm.setDuration(Duration.ofMinutes(100));

        oneMoreFilm.setName("Фильм 2");
        oneMoreFilm.setDescription("c".repeat(20));
        oneMoreFilm.setReleaseDate(LocalDate.now().minusYears(10));
        oneMoreFilm.setDuration(Duration.ofMinutes(100));

        unexistingFilm.setName("Фильм 3");
        unexistingFilm.setDescription("d".repeat(20));
        unexistingFilm.setReleaseDate(LocalDate.now().minusYears(11));
        unexistingFilm.setDuration(Duration.ofMinutes(120));

        popularFilm.setName("Фильм 4");
        popularFilm.setDescription("e".repeat(21));
        popularFilm.setReleaseDate(LocalDate.now().minusYears(10));
        popularFilm.setDuration(Duration.ofMinutes(60));


        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM films");
    }

    @Test
    public void addFilm() {
        film.setMpa(new Mpa(1));
        film.setGenres(Set.of(new Genre(2)));
        filmService.addFilm(film);

        Assertions.assertFalse(filmService.getAllFilms().isEmpty());
    }

    @Test
    public void addFilmLongDescr() {
        film.setMpa(new Mpa(1));
        film.setGenres(Set.of(new Genre(2)));
        film.setDescription("a".repeat(201));

        Assertions.assertThrows(ValidationException.class, () -> filmService.addFilm(film));
    }

    @Test
    public void updateFilm() {
        film.setMpa(new Mpa(1));
        film.setGenres(Set.of(new Genre(1)));
        Film newFilm = filmService.addFilm(film);
        newFilm.setGenres(Set.of(new Genre(3), new Genre(2)));
        Film filmUpdated = filmService.updateFilm(newFilm);

        Assertions.assertEquals(filmService.getById(newFilm.getId()).getName(),
                filmService.getById(filmUpdated.getId()).getName());
    }

    @Test
    public void getById() {
        film.setMpa(new Mpa(1));
        film.setGenres(Set.of(new Genre(1)));
        Film newFilm = filmService.addFilm(film);

        Assertions.assertEquals(newFilm, filmService.getById(newFilm.getId()));
    }

    @Test
    public void getByNotExistsId() {
        Assertions.assertThrows(NotFoundException.class, () -> filmService.getById(99L));
    }

    @Test
    public void getAllFilms() {
        film.setMpa(new Mpa(1));
        film.setGenres(Set.of(new Genre(1)));
        filmService.addFilm(film);
        popularFilm.setMpa(new Mpa(1));
        popularFilm.setGenres(Set.of(new Genre(1), new Genre(2)));
        filmService.addFilm(popularFilm);

        Assertions.assertEquals(2, filmService.getAllFilms().size());
    }

    @Test
    public void getAllFilmsIsEmpty() {
        Assertions.assertTrue(filmService.getAllFilms().isEmpty());
    }

    @Test
    public void getPopularMovies() {
        User newUser = userService.createUser(user);
        updatedFilm.setMpa(new Mpa(3));
        updatedFilm.setGenres(Set.of(new Genre(1), new Genre(2)));
        Film newFilm = filmService.addFilm(updatedFilm);
        popularFilm.setMpa(new Mpa(4));
        popularFilm.setGenres(Set.of(new Genre(2), new Genre(3)));
        Film addLikedMovie = filmService.addFilm(popularFilm);
        filmService.addLike(addLikedMovie.getId(), newUser.getId());
        List<Film> films = filmService.getPopularFilms(1);

        Assertions.assertTrue(films.contains(addLikedMovie));
    }

    @Test
    public void addLike() {
        User newUser = userService.createUser(user);
        unexistingFilm.setMpa(new Mpa(3));
        unexistingFilm.setGenres(Set.of(new Genre(1), new Genre(2)));
        Film newFilm = filmService.addFilm(unexistingFilm);
        filmService.addLike(newFilm.getId(), newUser.getId());

        Assertions.assertEquals(1, filmService.getPopularFilms(1).size());
    }

    @Test
    public void addLikeIsLiked() {
        User thisUser = userService.createUser(user);
        oneMoreFilm.setMpa(new Mpa(3));
        oneMoreFilm.setGenres(Set.of(new Genre(1), new Genre(2)));
        Film thisOneMoreFilm = filmService.addFilm(oneMoreFilm);
        filmService.addLike(thisOneMoreFilm.getId(), thisUser.getId());

        Assertions.assertThrows(NotFoundException.class,
                () -> filmService.addLike(thisOneMoreFilm.getId(), thisUser.getId()));
    }

    @Test
    public void disaddLikeNotLiked() {
        userService.createUser(user);
        film.setMpa(new Mpa(3));
        film.setGenres(Set.of(new Genre(1), new Genre(2)));
        filmService.addFilm(film);

        Assertions.assertThrows(NotFoundException.class,
                () -> filmService.removeLike(film.getId(), user.getId()));
    }
}