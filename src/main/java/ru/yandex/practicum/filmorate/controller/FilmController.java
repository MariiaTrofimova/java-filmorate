package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    @Autowired
    private final FilmService service;

    public FilmController (FilmService service) {
        this.service = service;
    }

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    @GetMapping
    public List<Film> listFilms() {
        return service.listFilms();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) throws JsonProcessingException {
        return service.addFilm(film);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Film updateFilm(@Valid @RequestBody Film film) throws JsonProcessingException {
        return service.updateFilm(film);
    }
}