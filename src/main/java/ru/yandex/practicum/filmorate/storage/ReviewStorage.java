package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    Review findReviewById(long id);

    boolean deleteReviewById(long id);

/*    List<Review> findAllReview();

    long findUsefulByReviewID(long id);*/

    List<Review> findTopReviews(int count);

    List<Review> findTopReviewsByFilmId(Long filmId, int count);
}