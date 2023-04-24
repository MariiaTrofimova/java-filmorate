package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    private final Mpa mpa;
    private final Set<Integer> genres = new HashSet<>(); //id жанров
    private final Set<Long> likes = new HashSet<>(); //id друзей, поставивших лайки

    public void addLike(long id) {
        likes.add(id);
    }

    public boolean deleteLike(long id) {
        return likes.remove(id);
    }

    public Set<Integer> getGenres() {
        return genres;
    }

    public void addGenre(int id) {
        genres.add(id);
    };

    public boolean deleteGenre(int id) {
        return genres.remove(id);
    };

    public Map<String,Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());
        return values;
    }
}
