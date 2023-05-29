package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review findReviewById(long id);

    List<Review> findReviewsByFilmId(Optional<Long> filmId, int count);

    Review addReview(Review review);

    Review updateReview(Review review);

    boolean deleteReviewById(long id);

    boolean addLikeReview(long id, long userId);

    boolean addDislikeReview(long id, long userId);

    boolean deleteLikeReview(long id, long userId);

    boolean deleteDislikeReview(long id, long userId);
}
