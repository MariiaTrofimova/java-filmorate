package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Review {
    private long reviewId;
    @NotBlank(message = "Отсутствует описание отзыва")
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull(message = "Отсутствует автор отзыва")
    private Long userId;
    @NotNull(message = "Отсутствует фильм отзыва")
    private Long filmId;
    private long useful; // рейтинг отзыва =  сумма положительных + отрицательные

    @JsonGetter
    public boolean getIsPositive() {
        return isPositive;
    }

    @JsonSetter
    public void setIsPositive(boolean positive) {
        isPositive = positive;
    }


    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);
        return values;
    }
}

