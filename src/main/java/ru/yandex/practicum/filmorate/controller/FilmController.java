package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.InMemoryFilmService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final InMemoryFilmService service;

    @Autowired
    public FilmController(InMemoryFilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Film> listFilms() {
        return service.listFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable long id) {
        return service.findFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> listTopFilms(
            @RequestParam(defaultValue = "10") Integer count) {
        return service.listTopFilms(count);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Film updateFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public List<Long> addLike(@PathVariable long id, @PathVariable long userId) {
        return service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public List<Long> deleteLike(@PathVariable long id, @PathVariable long userId) {
        return service.deleteLike(id, userId);
    }
}