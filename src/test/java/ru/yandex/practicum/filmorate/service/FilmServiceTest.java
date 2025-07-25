package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmServiceTest {

    private final FilmService filmService;
    private final UserService userService;



    @Test
    void addFilm() {

        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film);
        assertEquals(1, filmService.getAllFilms().size());

        System.out.println(film);
    }

    @Test
    void updateFilm() {

        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film);
        film.setName("New name");
        film.setDuration(Duration.ofMinutes(20));
        filmService.updateFilm(film);

        assertEquals("New name", filmService.getById(film.getId()).getName());
        assertEquals(Duration.ofMinutes(20), filmService.getById(film.getId()).getDuration());
    }

    @Test
    void GetFilmById() {

        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film);


        Film film2 = filmService.getById(film.getId());

        assertEquals("Valid Film", film2.getName());
    }

    @Test
    void GetAllFilms() {

        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film);
        Film film2 = new Film();
        film2.setName("Valid Film");
        film2.setDescription("Valid description");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film2);

        List<Film> films = filmService.getAllFilms();

        System.out.println(films.get(0));
        System.out.println(films.get(1));

        assertEquals(2, films.size());
    }

    @Test
    void testAddLike() {

        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film);
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);

        filmService.addLike(film.getId(), user.getId());

        List<Film> likesFilm = filmService.getPopularFilms(10);

        assertEquals(likesFilm.size(), 1);
    }

    @Test
    void testRemoveLike() {

        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film);
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("invalidlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);

        filmService.addLike(film.getId(), user.getId());
        filmService.removeLike(film.getId(), user.getId());

        List<Integer> likesFilm = filmService.getFilmById(film.getId()).getLikes().stream().toList();

        assertEquals(likesFilm.size(), 0);
    }

    @Test
    public void testGetPopularFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film);

        Film film2 = new Film();
        film2.setName("Valid Film");
        film2.setDescription("Valid description");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film2);

        Film film3 = new Film();
        film3.setName("Valid Film");
        film3.setDescription("Valid description");
        film3.setReleaseDate(LocalDate.now());
        film3.setDuration(Duration.ofMinutes(120));
        filmService.addFilm(film3);

        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("invalidlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userService.createUser(user);

        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film2.getId(), user.getId());

        List<Film> popularFilm = filmService.getPopularFilms(2);

        System.out.println(popularFilm.get(0));

        assertEquals(popularFilm.size(), 2);
    }
}