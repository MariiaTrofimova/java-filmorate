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
    @NotNull(message = "Отсутствует eventId события")
    private Long eventId;

    @NotNull(message = "Отсутствует entityId события")
    private Long entityId;

    @NotNull(message = "Отсутствует автор события")
    private Long userId;

    @NotNull(message = "Отсутствует время события")
    private Long timestamp;

    @NotNull(message = "Отсутствует тип события")
    private EventType eventType;

    @NotNull(message = "Отсутствует вид операции")
    private Operation operation;
}
