package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class Feed {
    @NotEmpty
    private long eventId;
    @NotEmpty
    private long entityId;
    @NotEmpty
    private long userId;
    @NotEmpty
    private long timestamp;
    @NotEmpty
    private EventType eventType;
    @NotEmpty
    private Operation operation;
}
