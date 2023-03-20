package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService service;

    public FilmController (FilmService service) {
        this.service = service;
    }

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    @GetMapping
    public List<Film> listFilms() {
        return service.listFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws JsonProcessingException {
        return service.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws JsonProcessingException {
        return service.updateFilm(film);
    }
}