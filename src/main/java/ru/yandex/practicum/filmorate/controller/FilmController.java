package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(@Qualifier("DbFilmService") FilmService service) {
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
            @RequestParam(defaultValue = "10") int count,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<Integer> genreId) {
        return service.listTopFilms(count, year, genreId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> listFilmsByOneDirector(
            @PathVariable long directorId,
            @RequestParam Optional<String> sortBy) {
        return service.listFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> findFilmsByTitleAndDirector(
            @RequestParam String query,
            @RequestParam String[] by) {
        return service.findFilmsByQuery(query, by);
    }

    @GetMapping("/common")
    public List<Film> findCommonFilms(
            @RequestParam Long userId,
            @RequestParam Long friendId
    ) {
        return service.findCommonFilms(userId, friendId);
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

    @DeleteMapping("/{id}")
    public boolean deleteFilm(@PathVariable long id) {
        return service.deleteFilm(id);
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