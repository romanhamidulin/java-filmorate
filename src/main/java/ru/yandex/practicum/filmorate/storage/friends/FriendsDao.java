package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsDao {

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId1, Long userId2);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    boolean isFriend(Long userId, Long friendId);
}
