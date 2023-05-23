package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewDao {

    Review addReview(Review review);

    Review updateReview(Review review);

    Review findReviewById(long id);

    boolean deleteReviewById(long id);
}
