package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface LikeDao {
    List<Long> getLikesByFilm(long filmId);

    boolean addLike(long filmId, long userId);

    boolean deleteLike(long filmId, long userId);
}
