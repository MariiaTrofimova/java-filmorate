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
import java.util.*;
import java.util.stream.Collectors;

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
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Director> directors = new HashSet<>();
    private final Set<Long> likes = new HashSet<>(); //id пользователей, поставивших лайки
    //я бы оставила как параметр фильма, удалила лишние dao и модели, являющиеся не сущностями, а связями

    public void addLike(long id) {
        likes.add(id);
    }

    public boolean deleteLike(long id) {
        return likes.remove(id);
    }

    public List<Genre> getGenres() {
        return genres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public boolean deleteGenre(Genre genre) {
        return genres.remove(genre);
    }

    public List<Director> getDirectors() {
        return directors.stream().sorted(Comparator.comparingInt(Director::getId)).collect(Collectors.toList());
    }

    public void addDirector(Director director) {
        directors.add(director);
    }

    public boolean deleteDirector(Director director) {
        return directors.remove(director);
    }


    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());
        return values;
    }
}