package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Map;
import java.util.Set;

public interface ReviewLikesDao {

    Map<Long, Set<ReviewLike>> findLikesByListReviews(Set<Long> ids);
}
