package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DbFeedService implements FeedService {
    private final FeedStorage feedStorage;

    public void add(long idEntity, long idUser, EventType eventType, Operation operation) {
        long timestamp = Instant.now().toEpochMilli();
        feedStorage.addFeed(idEntity, idUser, timestamp, eventType, operation);
    }

    public List<Feed> getByUserId(long id) {
        return feedStorage.findByUserId(id);
    }
}
