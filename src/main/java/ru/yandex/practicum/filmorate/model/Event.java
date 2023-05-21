package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    private LocalDateTime timestamp;
    private long userId;
    private EventType eventType;
    private Operation operation;
    private long eventId;
    private long entityId;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        ADD,
        REMOVE,
        UPDATE
    }
}
