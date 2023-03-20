package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private int nextFilmId = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public List<Film> listFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film addFilm(Film film) throws JsonProcessingException {
        if (FilmValidation.isFilmValid(film)) {
            int id = nextFilmId++;
            film.setId(id);
            films.put(id, film);
            log.debug("Фильм {} сохранен", mapper.writeValueAsString(film));
        }
        return film;
    }

    public Film updateFilm(Film film) throws JsonProcessingException {
        if (FilmValidation.isFilmValid(film)) {
            int id = film.getId();
            if (id == 0) {
                log.warn("Отсутствует id");
                throw new NotFoundException("Отсутствует id");
            } else if (!films.containsKey(film.getId())) {
                log.warn("Фильм с id {} не найден", id);
                throw new NotFoundException("Фильм с id " + id + " не найден");
            } else {
                films.put(id, film);
                log.debug("Фильм {} обновлен", mapper.writeValueAsString(film));
            }
        }
        return film;
    }
}
