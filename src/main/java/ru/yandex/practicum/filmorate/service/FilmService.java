package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;

    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public List<Long> addLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);
        film.addLike(userId);
        return new ArrayList<>(film.getLikes());
    }

    public List<Long> deleteLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);
        if (!film.deleteLike(userId)) {
            log.warn("Пользователь c id {} не ставил лайки фильму c id {}", userId, filmId);
            throw new NotFoundException(
                    String.format("Пользователь c id %d не ставил лайки фильму c id %d",
                            userId, filmId));
        }
        return new ArrayList<>(film.getLikes());
    }

    public List<Film> listTopFilms(Integer count) {
        return listFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> listFilms() {
        return storage.listFilms();
    }

    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    public Film findFilmById(long id) {
        return storage.findFilmById(id);
    }
}
