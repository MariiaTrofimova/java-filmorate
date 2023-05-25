package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
public class Feed {

    private long eventId;

    private long entityId;

    private long userId;

    private long timestamp;

    private EventType eventType;

    private Operation operation;
}
