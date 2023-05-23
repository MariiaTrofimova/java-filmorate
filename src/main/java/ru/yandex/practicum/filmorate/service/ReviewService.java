package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review findReviewById(long id);

    List<Review> findReviewsByFilmId(long filmId, int count);

    Review addReview(Review review);

    Review updateReview(Review review);

    boolean deleteReviewById(long id);

    List<Long> addLikeReview(long id, long userId);

    List<Long> addDislikeReview(long id, long userId);

    List<Long> deleteLikeReview(long id, long userId);

    List<Long> deleteDislikeReview(long id, long userId);
}
