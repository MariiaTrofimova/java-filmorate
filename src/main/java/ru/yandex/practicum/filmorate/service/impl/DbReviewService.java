package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.enums.Operation.*;

@Service("ReviewDaoImpl")
public class DbReviewService implements ReviewService {

    private final ReviewStorage reviewStorage;

    private final ReviewLikesStorage reviewLikesStorage;

    private final UserService userService;

    private final FilmService filmService;

    private final FeedStorage feedStorage;

    @Autowired
    public DbReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
                           ReviewLikesStorage reviewLikesStorage, UserService userService, FilmService filmService,
                           FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.reviewLikesStorage = reviewLikesStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.feedStorage = feedStorage;
    }

    @Override
    public Review findReviewById(long id) {
        return reviewStorage.findReviewById(id);
    }

    @Override
    public List<Review> findReviewsByFilmId(Optional<Long> filmId, int count) {
        if (filmId.isEmpty()) {
            return reviewStorage.findTopReviews(count);
        } else {
            return reviewStorage.findTopReviewsByFilmId(filmId.get(), count);
        }
    }

    @Override
    public Review addReview(Review review) {
        userService.findUserById(review.getUserId());
        filmService.findFilmById(review.getFilmId());
        review = reviewStorage.addReview(review);
        feedStorage.addFeed(review.getReviewId(), review.getUserId(), REVIEW, ADD);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        review = reviewStorage.updateReview(review);
        feedStorage.addFeed(review.getReviewId(), review.getUserId(), REVIEW, UPDATE);
        return review;
    }

    @Override
    public boolean deleteReviewById(long id) {
        Review review = reviewStorage.findReviewById(id);
        feedStorage.addFeed(review.getReviewId(), review.getUserId(), REVIEW, REMOVE);
        return reviewStorage.deleteReviewById(id);
    }

    //лайки отзывам
    @Override
    public boolean addLikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesStorage.addLikeReview(id, userId);
    }

    @Override
    public boolean addDislikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesStorage.addDislikeReview(id, userId);
    }

    @Override
    public boolean deleteLikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesStorage.deleteLikeReview(id, userId);
    }

    @Override
    public boolean deleteDislikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesStorage.deleteDislikeReview(id, userId);
    }
}