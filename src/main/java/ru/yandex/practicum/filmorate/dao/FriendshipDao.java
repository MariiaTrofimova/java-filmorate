package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendshipDao {
    List<Long> getFriendsByUser(long id);

    boolean addFriend(long userId, long friendId);

    boolean updateFriend(long userId, long friendId, boolean status);

    boolean deleteFriend(long userId, long friendId);
}
