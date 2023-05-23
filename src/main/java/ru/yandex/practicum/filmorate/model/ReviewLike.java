package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.message.Message;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReviewLike {
    private long reviewId;
    @NotNull(message = "Не указан автор лайка/дизлайка отзыву")
    private long likeId;
    @NotNull(message = "Не указан лайка или дизлайка отзыву")
    private boolean isPositive;
}
