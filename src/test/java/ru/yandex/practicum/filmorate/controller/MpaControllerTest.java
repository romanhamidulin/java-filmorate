package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaDbService;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTest {
    private final MpaDbService mpaService;
    private final Mpa g = new Mpa(1, "G");
    private final Mpa pg = new Mpa(2, "PG");
    private final Mpa r = new Mpa(4, "R");

    @Test
    public void getMpaById() {
        Assertions.assertEquals(g, mpaService.getMpaById(1));
    }

    @Test
    public void getMpaNameById() {
        Mpa mpa = mpaService.getMpaById(3);

        Assertions.assertEquals(mpa.getName(), "PG-13");
    }

    @Test
    public void getMpaByIdNonExists() {
        Assertions.assertThrows(NotFoundException.class,
                () -> mpaService.getMpaById(139));
    }

    @Test
    public void getMpaList() {
        Assertions.assertTrue(mpaService.getMpaList().contains(pg));
        Assertions.assertTrue(mpaService.getMpaList().contains(r));
    }
}