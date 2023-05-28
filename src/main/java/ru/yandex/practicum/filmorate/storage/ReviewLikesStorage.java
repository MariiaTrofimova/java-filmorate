package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;

public interface ReviewLikesStorage {
    boolean addLikeReview(long reviewId, long userId);

    boolean addDislikeReview(long reviewId, long userId);

    boolean deleteLikeReview(long reviewId, long userId);

    boolean deleteDislikeReview(long reviewId, long userId);

    Map<Long, Long> getUsefulByReviewList(List<Long> ids);
}