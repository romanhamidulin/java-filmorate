package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Test
    @Order(1)
    public void createFilm() {
        Film newFilm = new Film();
        newFilm.setName("test_film");
        newFilm.setDescription("test_description");
        newFilm.setReleaseDate(LocalDate.of(1996, 4, 18));
        newFilm.setDuration(Duration.ofMinutes(100));
        newFilm.setMpa(mpaStorage.getMpaById(1).get());
        newFilm.setGenres(Set.of(genreStorage.getGenreById(1).get()));
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.addFilm(newFilm));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(film).hasFieldOrPropertyWithValue("name", "test_film");
                            assertThat(film).hasFieldOrPropertyWithValue("description", "test_description");
                            assertThat(film).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(100));
                            assertThat(film).hasFieldOrPropertyWithValue("mpa", mpaStorage.getMpaById(1).get());
                            assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                                    LocalDate.of(1996, 4, 18));
                        }
                );
    }

    @Test
    @Order(2)
    public void updateFilm() {
        Film newFilm = new Film();
        newFilm.setId(1L);
        newFilm.setName("test_film1");
        newFilm.setDescription("test_description1");
        newFilm.setReleaseDate(LocalDate.of(1996, 4, 18));
        newFilm.setDuration(Duration.ofMinutes(100));
        newFilm.setMpa(mpaStorage.getMpaById(1).get());
        newFilm.setGenres(Set.of(genreStorage.getGenreById(1).get()));
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.updateFilm(newFilm));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(film).hasFieldOrPropertyWithValue("name", "test_film1");
                            assertThat(film).hasFieldOrPropertyWithValue("description", "test_description1");
                        }
                );

    }

    @Test
    @Order(3)
    public void getFilmById() {
        Optional<Film> filmOptional = filmStorage.getById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(film).hasFieldOrPropertyWithValue("name", "test_film1");
                            assertThat(film).hasFieldOrPropertyWithValue("description", "test_description1");
                        }
                );
    }

    @Test
    @Order(4)
    public void getAllFilms() {
        Film newFilm = new Film();
        newFilm.setName("test_film2");
        newFilm.setDescription("test_description2");
        newFilm.setReleaseDate(LocalDate.of(1997, 4, 18));
        newFilm.setDuration(Duration.ofMinutes(120));
        newFilm.setMpa(mpaStorage.getMpaById(2).get());
        newFilm.setGenres(Set.of(genreStorage.getGenreById(2).get()));
        filmStorage.addFilm(newFilm);
        List<Film> films = filmStorage.getAllFilms();
        assertThat(films).asList().element(0).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(films).asList().element(1).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(films).asList().element(1).hasFieldOrPropertyWithValue("description", "test_description2");
    }

    @Test
    @Order(5)
    public void getTopPopularFilms() {
        List<Film> films = filmStorage.getTopPopularFilms(1);
        assertThat(films).asList().element(0).hasFieldOrPropertyWithValue("id", 1L);
    }
}