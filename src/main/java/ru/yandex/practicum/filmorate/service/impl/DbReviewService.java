package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.ReviewDao;
import ru.yandex.practicum.filmorate.storage.ReviewLikesDao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.enums.Operation.*;

@Service("ReviewDaoImpl")
public class DbReviewService implements ReviewService {

    private final ReviewDao reviewDao;

    private final ReviewLikesDao reviewLikesDao;

    private final UserService userService;

    private final FilmService filmService;

    private final FeedService feedService;

    @Autowired
    public DbReviewService(@Qualifier("reviewDaoImpl") ReviewDao reviewDao, ReviewLikesDao reviewLikesDao, UserService userService, FilmService filmService, FeedService feedService) {
        this.reviewDao = reviewDao;
        this.reviewLikesDao = reviewLikesDao;
        this.userService = userService;
        this.filmService = filmService;
        this.feedService = feedService;
    }

    @Override
    public Review findReviewById(long id) {
        return getReviewWithUseful(reviewDao.findReviewById(id));
    }

    @Override
    public List<Review> findReviewsByFilmId(Optional<Long> filmId, int count) {
        List<Review> topReviews;
        if (filmId.isEmpty()) {
            topReviews = reviewDao.findAllReview();
        } else {
            topReviews = reviewDao.findAllReviewsByFilmId(filmId.get());
        }
        topReviews = getReviewsWithUseful(topReviews);
        return topReviews.stream()
                .sorted((r1, r2) -> Math.toIntExact(r2.getUseful() - r1.getUseful()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Review addReview(Review review) {
        userService.findUserById(review.getUserId());
        filmService.findFilmById(review.getFilmId());
        review = reviewDao.addReview(review);
        feedService.add(review.getReviewId(), review.getUserId(), REVIEW, ADD);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        review = reviewDao.updateReview(review);
        feedService.add(review.getReviewId(), review.getUserId(), REVIEW, UPDATE);
        return review;
    }

    @Override
    public boolean deleteReviewById(long id) {
        Review review = reviewDao.findReviewById(id);
        feedService.add(review.getReviewId(), review.getUserId(), REVIEW, REMOVE);
        return reviewDao.deleteReviewById(id);
    }

    //лайки отзывам
    @Override
    public boolean addLikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesDao.addLikeReview(id, userId);
    }

    @Override
    public boolean addDislikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesDao.addDislikeReview(id, userId);
    }

    @Override
    public boolean deleteLikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesDao.deleteLikeReview(id, userId);
    }

    @Override
    public boolean deleteDislikeReview(long id, long userId) {
        findReviewById(id);
        userService.findUserById(userId);
        return reviewLikesDao.deleteDislikeReview(id, userId);
    }

    private List<Review> getReviewsWithUseful(List<Review> reviews) {
        List<Long> reviewIds = reviews.stream()
                .map(Review::getReviewId).collect(Collectors.toList());
        Map<Long, Long> usefulByReview = reviewLikesDao.getUsefulByReviewList(reviewIds);
        return reviews.stream()
                .peek(review -> {
                    long useful = usefulByReview.getOrDefault(review.getReviewId(), 0L);
                    review.setUseful(useful);
                })
                .collect(Collectors.toList());
    }

    private Review getReviewWithUseful(Review review) {
        long useful = reviewDao.findUsefulByReviewID(review.getReviewId());
        review.setUseful(useful);
        return review;
    }
}