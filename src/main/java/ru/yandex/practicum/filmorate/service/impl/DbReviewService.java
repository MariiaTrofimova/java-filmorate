package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.ReviewDao;
import ru.yandex.practicum.filmorate.storage.ReviewLikesDao;

import java.util.*;

@Service("ReviewDaoImpl")
public class DbReviewService implements ReviewService {

    private final ReviewDao reviewDao;

    private final ReviewLikesDao reviewLikesDao;

    @Autowired
    public DbReviewService(@Qualifier("reviewDaoImpl") ReviewDao reviewDao, ReviewLikesDao reviewLikesDao) {
        this.reviewDao = reviewDao;
        this.reviewLikesDao = reviewLikesDao;
    }

    @Override
    public Review findReviewById(long id) {
        return null;
    }

    @Override
    public List<Review> findReviewsByFilmId(long filmId,  int count) {
        Set<Long> reviewId = Collections.singleton(reviewDao.findReviewById(filmId).getId());
        Map<Long, Set<ReviewLike>> listLikesByReviewId = reviewLikesDao.findLikesByListReviews(reviewId);
        updateUsefull(listLikesByReviewId, count);
        return new ArrayList<>();
    }

    @Override
    public Review addReview(Review review) {
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public boolean deleteReviewById(long id) {
        return reviewDao.deleteReviewById(id);
    }

    //лайки отзывам
    @Override
    public List<Long> addLikeReview(long id, long userId) {
        return null;
    }

    @Override
    public List<Long> addDislikeReview(long id, long userId) {
        return null;
    }

    @Override
    public List<Long> deleteLikeReview(long id, long userId) {
        return null;
    }

    @Override
    public List<Long> deleteDislikeReview(long id, long userId) {
        return null;
    }

    private List<Review> updateUsefull(Map<Long, Set<ReviewLike>> likes, int count) {
        List<Review> reviews = new ArrayList<>();
        for (long id: likes.keySet()) {
            long usefull = 0;
            for (ReviewLike like: likes.get(id)) {
                if(like.isPositive() == true) {
                    usefull = +1;
                } else if (like.isPositive() == false) {
                    usefull = -1;
                }
            }
            reviews.get((int) id).setUseful(usefull);
        }
        return new ArrayList<>();
    }
}
