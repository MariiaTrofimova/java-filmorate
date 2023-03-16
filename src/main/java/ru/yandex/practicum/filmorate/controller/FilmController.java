package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int nextFilmId = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @GetMapping
    public List<Film> listFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws JsonProcessingException {
        if (FilmValidation.isFilmValid(film)) {
            int id = nextFilmId++;
            film.setId(id);
            films.put(id, film);
            log.debug("Фильм {} сохранен", mapper.writeValueAsString(film));
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws JsonProcessingException {
        if (FilmValidation.isFilmValid(film)) {
            int id = film.getId();
            if (id == 0) {
                log.warn("Отсутствует id");
                throw new ValidationException("Отсутствует id");
            } else if (!films.containsKey(film.getId())) {
                log.warn("Фильм с id {} не найден", id);
                throw new ValidationException("Фильм с id " + id + " не найден");
            } else {
                films.put(id, film);
                log.debug("Фильм {} обновлен", mapper.writeValueAsString(film));
            }

        }
        return film;
    }
}