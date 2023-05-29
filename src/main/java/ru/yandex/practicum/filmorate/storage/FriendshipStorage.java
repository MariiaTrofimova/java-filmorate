package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FriendshipStorage {
    List<Long> getFriendsByUser(long id);

    boolean addFriend(long userId, long friendId);

    boolean updateFriend(long userId, long friendId, boolean status);

    boolean deleteFriend(long userId, long friendId);
}
