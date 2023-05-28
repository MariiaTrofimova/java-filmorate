package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private static final String SQL_FIND_BY_USER_ID = "SELECT * FROM feed WHERE id_user = ? ORDER BY CREATED_TS";
    private static final String SQL_ADD_FEED = "INSERT INTO feed(id_entity, id_user, event_type, operation)" +
            "VALUES (?, ?, ?, ?)";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> findByUserId(Long id) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_ID, (rs, rowNum) -> mapRowToFeed(rs), id);
    }

    @Override
    public void addFeed(Long idEntity, Long idUser, EventType eventType, Operation operation) {
        jdbcTemplate.update(SQL_ADD_FEED, idEntity, idUser, eventType.toString(), operation.toString());
    }

    private Feed mapRowToFeed(ResultSet rs) throws SQLException {
        long feedId = rs.getLong("id_event");
        long entityId = rs.getLong("id_entity");
        long userId = rs.getLong("id_user");
        Long timestamp = rs.getTimestamp("created_ts").toInstant().toEpochMilli();
        EventType eventType = EventType.valueOf(rs.getString("event_type"));
        Operation operation = Operation.valueOf(rs.getString("operation"));

        return Feed.builder()
                .eventId(feedId)
                .entityId(entityId)
                .userId(userId)
                .timestamp(timestamp)
                .eventType(eventType)
                .operation(operation)
                .build();
    }
}
