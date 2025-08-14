package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeStorage likeStorage;

    public void addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        likeStorage.removeLike(filmId, userId);
    }
}
