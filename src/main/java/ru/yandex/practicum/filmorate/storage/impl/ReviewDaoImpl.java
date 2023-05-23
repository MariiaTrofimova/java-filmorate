package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDao;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO Review (content, is_positive, user_id, film_id) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        review.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Отзыв с id {} сохранен", review.getId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "update reviews set " + "content = ?, is_positive = ?" + "where review_id = ?";
        if (jdbcTemplate.update(sql, review.getContent(), review.isPositive(), review.getId()) > 0) {
            return review;
        }
        log.warn("Отзыв с id {} не найден", review.getId());
        throw new NotFoundException(String.format("Отзыв с id %d не найден", review.getId()));
    }

    @Override
    public boolean deleteReviewById(long id) {
        String sql = "delete from reviews where review_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Review findReviewById(long id) {
        String sql = "select *,  from review_likes  where review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToReview(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Отзыв с id {} не найден", id);
            throw new NotFoundException(String.format("Отзыв с id %d не найден", id));
        }
    }

    public Set<Review> findReviewsByFilmId(long film_id) {
        if (film_id == -1) {
            String sql = "select * " + "from reviews ";
            Set<Review> reviews = (Set<Review>) jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToReview(rs));
            return reviews;

        } else {
            String sql = "select * " + "from reviews " + "where film_id = ?";
            Set<Review> reviews = (Set<Review>) jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToReview(rs), film_id);
            return reviews;
        }
    }

    private void updateUsefulReview(Set<Review> reviews) {

    }
    private Review mapRowToReview(ResultSet rs) throws SQLException {
        long id = rs.getLong("review_id");
        String content = rs.getString("content");
        boolean isPositive = rs.getBoolean("is_positive");
        long user_id = rs.getLong("user_id");
        long film_id = rs.getLong("film_id");

        return Review.builder()
                .id(id)
                .content(content)
                .isPositive(isPositive)
                .userId(user_id)
                .filmId(film_id)
                .build();
    }
}
