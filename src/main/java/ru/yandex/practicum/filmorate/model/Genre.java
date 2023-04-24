package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Genre {
    private int id;
    @Size(min = 1, max = 50, message = "Длина названия жанра должна быть от 1 до 50 символов")
    private String name;
}
