package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendsDao {
    void addFriend(Long userId, Long friendId, boolean isFriend);

    void deleteFriend(Long userId, Long friendId);

    List<Long> getFriends(Long userId);

    Friendship getFriend(Long userId, Long friendId);

    boolean isFriend(Long userId, Long friendId);
}
