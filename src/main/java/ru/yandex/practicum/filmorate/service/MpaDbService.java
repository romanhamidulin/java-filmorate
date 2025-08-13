package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaDbService {
    private final MpaDao mpaDao;

    public Mpa getMpaById(int id) {
        if (!mpaDao.isContains(id)) {
            throw new NotFoundException("Рейтинг не найден");
        }
        return mpaDao.getMpaById(id);
    }

    public List<Mpa> getMpaList() {
        return mpaDao.getMpaList();
    }
}
