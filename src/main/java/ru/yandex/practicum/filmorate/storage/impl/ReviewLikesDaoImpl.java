package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.ReviewLikesDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ReviewLikesDaoImpl implements ReviewLikesDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public ReviewLikesDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public boolean addLikeReview(long review_id, long user_id) {
        String sql = "merge into review_likes (review_id, user_id, is_positive) values (?, ? , 1)";
        return jdbcTemplate.update(sql, review_id, user_id) > 0;
    }

    @Override
    public boolean addDislikeReview(long review_id, long user_id) {
        String sql = "merge into review_likes (review_id, user_id, is_positive) values(?, ?, -1)";
        return jdbcTemplate.update(sql, review_id, user_id) > 0;
    }

    @Override
    public boolean deleteLikeReview(long review_id, long user_id) {
        String sql = "delete from review_likes where review_id = ? and user_id = ? and is_positive = 1";
        return jdbcTemplate.update(sql, review_id, user_id) > 0;
    }

    @Override
    public boolean deleteDislikeReview(long review_id, long user_id) {
        String sql = "delete from review_likes where review_id = ? and user_id = ?  and is_positive = -1";
        return jdbcTemplate.update(sql, review_id, user_id) > 0;
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