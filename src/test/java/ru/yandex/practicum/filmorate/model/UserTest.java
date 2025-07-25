package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final Validator validator;

    public UserTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrect() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email должен быть корректным")));
    }

    @Test
    void whenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("invalid login");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не должен содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void whenBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameIsEmpty() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}