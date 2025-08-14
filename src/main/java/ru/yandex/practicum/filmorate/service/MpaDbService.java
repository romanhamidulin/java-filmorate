package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaDbService {

    private final MpaStorage mpaStorage;

    public Mpa getMpaById(int mpaId) {
        return mpaStorage.getMpaById(mpaId).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getMpaList();
    }
}
