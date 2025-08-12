package ru.yandex.practicum.filmorate.storage.like;

public interface LikeDao {

    void addlike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    int countLikes(Long filmId);

    boolean isLiked(Long filmId, Long userId);
}
