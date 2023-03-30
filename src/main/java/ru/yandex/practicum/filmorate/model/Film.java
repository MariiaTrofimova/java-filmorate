package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import ru.yandex.practicum.filmorate.validation.ReleaseConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {
    private long id;
    @NotBlank(message = "Отсутствует название")
    private String name;
    @Size(min = 1, max = 200, message = "Длина описания должна быть от 1 до 200 символов")
    private String description;
    @NotNull(message = "Отсутствует дата релиза")
    @ReleaseConstraint
    private LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма отрицательная")
    private int duration;
    private final Set<Long> likes = new HashSet<>(); //id друзей, поставивших лайки

    public void addLike(long id) {
        likes.add(id);
    }

    public boolean deleteLike(long id) {
        if (!likes.contains(id)) {
            return false;
        }
        likes.remove(id);
        return true;
    }
}
