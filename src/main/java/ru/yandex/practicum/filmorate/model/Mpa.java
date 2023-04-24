package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mpa {
    private int id;
    @Size(min = 1, max = 5, message = "Длина названия рейтинга должна быть от 1 до 5 символов")
    private String name;
    @Size(min = 1, max = 100, message = "Длина описания должна быть от 1 до 100 символов")
    private String description;
}
