package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {
    private int id;
    @NotNull(message = "Отсутствует название")
    @NotBlank(message = "Отсутствует название")
    private String name;
    @NotNull(message = "Отсутствует описание")
    @NotBlank(message = "Отсутствует описание")
    @Size(max = 200, message = "Длина описания больше 200 символов")
    private String description;
    @NotNull(message = "Отсутствует дата релиза")
    @ReleaseConstraint
    private LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма отрицательная")
    private int duration;
}
