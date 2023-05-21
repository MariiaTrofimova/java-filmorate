package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventStorage {
    void save(long entity_id, long user_id, LocalDateTime timestamp, String event_type, String operation);

    List<Event> findEventsUser(long id);
}
