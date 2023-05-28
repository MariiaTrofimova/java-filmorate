package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ReviewLikesDbStorage implements ReviewLikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public ReviewLikesDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public boolean addLikeReview(long reviewId, long userId) {
        String sql = "merge into review_likes (review_id, user_id, is_positive) values (?, ? , 1)";
        return jdbcTemplate.update(sql, reviewId, userId) > 0;
    }

    @Override
    public boolean addDislikeReview(long reviewId, long userId) {
        String sql = "merge into review_likes (review_id, user_id, is_positive) values(?, ?, -1)";
        return jdbcTemplate.update(sql, reviewId, userId) > 0;
    }

    @Override
    public boolean deleteLikeReview(long reviewId, long userId) {
        String sql = "delete from review_likes where review_id = ? and user_id = ? and is_positive = 1";
        return jdbcTemplate.update(sql, reviewId, userId) > 0;
    }

    @Override
    public boolean deleteDislikeReview(long reviewId, long userId) {
        String sql = "delete from review_likes where review_id = ? and user_id = ?  and is_positive = -1";
        return jdbcTemplate.update(sql, reviewId, userId) > 0;
    }

    @Override
    public Map<Long, Long> getUsefulByReviewList(List<Long> ids) {
        String sql = "select review_id, SUM(is_positive) as useful " +
                "from review_likes " +
                "where review_id in (:ids) " +
                "group by review_id";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        final Map<Long, Long> usefulByReview = new HashMap<>();
        namedJdbcTemplate.query(sql, parameters,
                rs -> {
                    long reviewId = rs.getLong("review_id");
                    long useful = rs.getInt("useful");
                    usefulByReview.put(reviewId, useful);
                });
        return usefulByReview;
    }
}