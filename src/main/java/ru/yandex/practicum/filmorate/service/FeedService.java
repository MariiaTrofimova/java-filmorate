package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface FeedService {
    void add(long idEntity, long idUser, EventType eventType, Operation operation);
    List<Feed> getByUserId(long id);
}
