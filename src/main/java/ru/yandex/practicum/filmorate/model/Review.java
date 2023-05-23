package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    private long id;
    @NotBlank(message = "Отсутствует описание отзыва")
    private String content;
    @NotNull
    private boolean isPositive;
    @NotNull(message = "Не найден автор отзыва")
    private long userId;
    @NotNull(message = "Не найден фильм отзыва")
    private long filmId;
    private long useful; // рейтинг отзыва =  сумма положительных + отрицательные

    public boolean isPositive() {
        return isPositive;
    }
}

