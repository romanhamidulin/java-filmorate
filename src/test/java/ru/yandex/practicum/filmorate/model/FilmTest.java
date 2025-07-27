package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;


import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    private final Validator validator;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrect() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        ValidationException e = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Название фильма не может быть пустым.", e.getMessage());
    }

    @Test
    void whenDescriptionIsLong() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        ValidationException e = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Максимальная длина описания — 200 символов.", e.getMessage());
    }

    @Test
    void whenReleaseDateIsNull() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(null);
        film.setDuration(Duration.ofMinutes(120));

        ValidationException e = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Дата релиза не может быть нулевой или раньше 1895-12-28", e.getMessage());
    }

    @Test
    void whenDurationIsNegative() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(-1));

        ValidationException e = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Продолжительность должна быть положительной", e.getMessage());
    }

    private static void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String message = "Дата релиза не может быть нулевой или раньше " + MIN_RELEASE_DATE;
            throw new ValidationException(message);
        }

        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            String message = "Продолжительность должна быть положительной";
            throw new ValidationException(message);
        }
    }
}