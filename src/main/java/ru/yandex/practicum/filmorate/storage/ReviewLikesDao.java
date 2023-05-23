package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;

public interface ReviewLikesDao {
    boolean addLikeReview(long review_id, long user_id);

    boolean addDislikeReview(long review_id, long user_id);

    boolean deleteLikeReview(long review_id, long user_id);

    boolean deleteDislikeReview(long review_id, long user_id);

    Map<Long, Long> getUsefulByReviewList(List<Long> ids);
}