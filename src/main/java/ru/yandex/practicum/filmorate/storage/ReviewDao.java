package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review addReview(Review review);

    Review updateReview(Review review);

    Review findReviewById(long id);

    boolean deleteReviewById(long id);

    List<Review> findAllReview();

    List<Review> findAllReviewsByFilmId(long filmId);

    long findUsefulByReviewID(long id);
}