package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> getLikesByFilm(long filmId) {
        String sql = "select user_id from likes where film_id =?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    @Override
    public List<Long> getTopFilmId(int count) {
        String sql = "select film_id from likes group by film_id order by count(user_id) desc limit ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), count);
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        String sql = "insert into likes(film_id, user_id) " +
                "values (?, ?)";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public boolean deleteLike(long filmId, long userId) {
        String sql = "delete from likes where (film_id = ? AND user_id = ?)";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }
}
