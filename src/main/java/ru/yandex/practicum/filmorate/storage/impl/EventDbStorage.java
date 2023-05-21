package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventDbStorage implements EventStorage {
    private static final String saveEventQuery = "INSERT INTO USER_EVENTS " +
            "(entity_id, user_id, timestamp, event_type, operation) VALUES (?, ?, ?, ?, ?)";
    private final String findEventsUser = "SELECT * FROM USER_EVENTS WHERE user_id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipDao friendshipStorage;

    public EventDbStorage(JdbcTemplate jdbcTemplate, @Lazy FriendshipDao friendshipStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public void save(long entityId, long userId, LocalDateTime timestamp, String eventType, String operation) {
        jdbcTemplate.update(saveEventQuery, entityId, userId, timestamp, eventType, operation);
    }

    public List<Event> findEventsUser(long id) {
        List<Event> eventsFriends = new ArrayList<>();
        List<Long> friends = friendshipStorage.getFriendsByUser(id);
        for (Long friend : friends) {
            eventsFriends.addAll(jdbcTemplate.query(findEventsUser, new EventRowMapper(), friend));
        }
        return eventsFriends;
    }

    private class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            String[] split = rs.getString("timestamp").split("\\.");
            LocalDateTime timestamp = LocalDateTime.parse(split[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return Event.builder()
                    .timestamp(timestamp)
                    .userId(rs.getLong("user_id"))
                    .eventType(Event.EventType.valueOf(rs.getString("event_type")))
                    .operation(Event.Operation.valueOf(rs.getString("operation")))
                    .eventId(rs.getLong("event_id"))
                    .entityId(rs.getLong("entity_id"))
                    .build();
        }
    }
}

