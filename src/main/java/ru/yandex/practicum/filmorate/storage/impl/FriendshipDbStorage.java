package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;

@Repository
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> getFriendsByUser(long id) {
        String sql = "select friend_id from friendship where user_id =? and status = true " +
                "union select user_id from friendship where friend_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), id, id);
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        String sql = "insert into friendship(user_id, friend_id, status) " +
                "values (?, ?, false)";
        return jdbcTemplate.update(sql, friendId, userId) > 0;
    }

    @Override
    public boolean updateFriend(long userId, long friendId, boolean status) {
        String sql = "update friendship set status = ? " +
                "where user_id = ? and friend_id = ?";
        return jdbcTemplate.update(sql, status, userId, friendId) > 0;
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        String sql = "delete from friendship where (user_id = ? AND friend_id = ?)";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }
}
