package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewLikesDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
@Slf4j
public class ReviewLikesDaoImpl implements ReviewLikesDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReviewLikesDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean addReviewLike(long review_id, long user_id) {
        String sql = "INSERT INTO Review_likes (review_id, like_id, is_positive) VALUES(?, ?, ?)";
        return jdbcTemplate.update(sql, review_id, user_id, true) > 0;
    }

    public boolean addReviewDislike(long review_id, long user_id) {
        String sql = "INSERT INTO Review_likes (review_id, like_id, is_positive) VALUES(?, ?, ?)";
        return jdbcTemplate.update(sql, review_id, user_id, false) > 0;
    }

    public boolean deleteLikeReview(long review_id, long user_id) {
        String sql = "delete from reviews where review_id = ?, user_id = ?";
        return jdbcTemplate.update(sql, review_id, user_id) > 0;
    }

    public boolean deleteDislikeReview(long review_id, long user_id) {
        String sql = "delete from reviews where review_id = ?, user_id = ?";
        return jdbcTemplate.update(sql, review_id, user_id) > 0;
    }

    @Override
    public Map <Long, Set<ReviewLike>> findLikesByListReviews(Set<Long> ids) {
        String sql = "* , from review_likes where review_id in (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        final Map<Long, Set<ReviewLike>> revieLikesByReviewsList = new HashMap<>();

        namedParameterJdbcTemplate.query(sql, parameters,
                rs -> {
                    long review_id = rs.getLong("review_id");
                    ReviewLike reviewLike = mapRowToReviewLikes(rs);
                    Set<ReviewLike> reviewLikes = revieLikesByReviewsList.getOrDefault(review_id, new HashSet<>());
                    reviewLikes.add(reviewLike);
                    revieLikesByReviewsList.put(review_id, reviewLikes);
                });
        return revieLikesByReviewsList;
    }

    private ReviewLike mapRowToReviewLikes(ResultSet rs) throws SQLException {
        long reviewId = rs.getLong("review_id");
        long likeId = rs.getLong("user_id");
        boolean isPositive = rs.getBoolean("is_positive");
        return ReviewLike.builder()
                .reviewId(reviewId)
                .likeId(likeId)
                .isPositive(isPositive)
                .build();
    }
}
