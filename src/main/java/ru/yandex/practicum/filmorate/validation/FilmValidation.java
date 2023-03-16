package ru.yandex.practicum.filmorate.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidation {
    public static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static boolean isFilmValid(Film film) throws JsonProcessingException {
        return isNameValid(film) && isDescriptionValid(film) && isReleaseDateValid(film) && isDurationValid(film);
    }

    private static boolean isDurationValid(Film film) throws JsonProcessingException {
        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма {} отрицательная", mapper.writeValueAsString(film.getDuration()));
            throw new ValidationException("Продолжительность фильма отрицательная");
        }
        return true;
    }

    private static boolean isReleaseDateValid(Film film) {
        if (film.getReleaseDate() == null) {
            log.warn("Отсутствует дата релиза");
            throw new ValidationException("Отсутствует дата релиза");
        } else if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            log.warn("Дата релиза {} раньше 28 декабря 1895 года", film.getReleaseDate());
            throw new ValidationException("Дата релиза раньше 28 декабря 1895 года");
        }
        return true;
    }

    private static boolean isDescriptionValid(Film film) {
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.warn("Отсутствует описание");
            throw new ValidationException("Отсутствует описание");
        } else if (film.getDescription().length() > 200) {
            log.warn("Длина описания:{}. Больше 200 символов.", film.getDescription().length());
            throw new ValidationException("Длина описания больше 200 символов");
        }
        return true;
    }

    private static boolean isNameValid(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Отсутствует название");
            throw new ValidationException("Отсутствует название");
        }
        return true;
    }

}
