package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("FeedDbStorage")
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedMapper feedRowMapper;

    @Override
    public List<Feed> findByUserId(Long id) {
        String sql = "SELECT * FROM feed WHERE id_user = ? ORDER BY timestamp ASC";

        return jdbcTemplate.query(sql, feedRowMapper, id);
    }

    @Override
    public void addFeed(Long idEntity, Long idUser, Long timestamp, EventType eventType, Operation operation) {
        String sql = "INSERT INTO feed(id_entity, id_user, timestamp, event_type, operation) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, idEntity, idUser, timestamp,
                eventType.toString(), operation.toString());
    }


    @Repository
    public static class FeedMapper implements RowMapper<Feed> {

        @Override
        public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Feed.builder()
                    .eventId(rs.getLong("id_event"))
                    .entityId(rs.getLong("id_entity"))
                    .userId(rs.getLong("id_user"))
                    .timestamp(rs.getLong("timestamp"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .build();
        }
    }
}
