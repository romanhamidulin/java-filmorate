package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    Mpa getMpaById(int id);

    List<Mpa> getMpaList();

    boolean isContains(int id);
}
