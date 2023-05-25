package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class Feed {

    @NotNull
    private Long eventId;
    @NotNull
    private Long entityId;
    @NotNull
    private Long userId;
    @NotNull
    private Long timestamp;
    @NotNull
    private EventType eventType;
    @NotNull
    private Operation operation;
}
