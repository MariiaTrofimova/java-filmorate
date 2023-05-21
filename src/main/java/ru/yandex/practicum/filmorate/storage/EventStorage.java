package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventStorage {
    void save(long entityId, long userId, LocalDateTime timestamp, String eventType, String operation);

    List<Event> findEventsUser(long id);
}
