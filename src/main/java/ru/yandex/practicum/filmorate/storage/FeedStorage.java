package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedStorage {
    List<Feed> findByUserId(long id);

    void addFeed(long idEntity, long idUser, long timestamp, EventType eventType, Operation operation);
   }
