package ru.yandex.practicum.filmorate.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private long nextFilmId = 1;
    private final HashMap<Long, Film> films = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public List<Film> listFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(long id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм с id {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id %s не найден", id));
        }
        return films.get(id);
    }

    @SneakyThrows
    @Override
    public Film addFilm(Film film) {
        long id = nextFilmId++;
        film.setId(id);
        films.put(id, film);
        log.debug("Фильм {} сохранен", mapper.writeValueAsString(film));
        return film;
    }

    @SneakyThrows
    @Override
    public Film updateFilm(Film film) {
        long id = film.getId();
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
        return film;
    }
}
