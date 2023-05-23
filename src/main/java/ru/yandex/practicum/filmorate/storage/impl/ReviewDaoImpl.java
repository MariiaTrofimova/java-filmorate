package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        long id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue();
        review.setReviewId(id);
        log.info("Отзыв с id {} сохранен", review.getReviewId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "update reviews set  content = ?, is_positive = ? where review_id = ?";
        Long reviewId = review.getReviewId();
        if (jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                reviewId) > 0) {
            review = findReviewById(reviewId);
            return review;
        }
        log.warn("Отзыв с id {} не найден", reviewId);
        throw new NotFoundException(String.format("Отзыв с id %d не найден", review.getReviewId()));
    }

    @Override
    public boolean deleteReviewById(long id) {
        String sql = "delete from reviews where review_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public List<Review> findAllReview() {
        String sql = "select * from reviews";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToReview(rs));
    }

    @Override
    public List<Review> findAllReviewsByFilmId(long filmId) {
        String sql = "select * from reviews where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToReview(rs), filmId);
    }

    @Override
    public long findUsefulByReviewID(long id) {
        String sql = "select SUM(is_positive) as useful " +
                "from review_likes  " +
                "where review_id = ? " +
                "group by review_id";
        List<Long> list = jdbcTemplate.queryForList(sql, Long.class, id);
        if (list.isEmpty()) {
            return 0L;
        } else return list.get(0);
    }

    @Override
    public Review findReviewById(long id) {
        String sql = "select * from reviews where review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToReview(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Отзыв с id {} не найден", id);
            throw new NotFoundException(String.format("Отзыв с id %d не найден", id));
        }
    }

    private Review mapRowToReview(ResultSet rs) throws SQLException {
        long id = rs.getLong("review_id");
        String content = rs.getString("content");
        boolean isPositive = rs.getBoolean("is_positive");
        long user_id = rs.getLong("user_id");
        long film_id = rs.getLong("film_id");

        return Review.builder()
                .reviewId(id)
                .content(content)
                .isPositive(isPositive)
                .userId(user_id)
                .filmId(film_id)
                .build();
    }
}