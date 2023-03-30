package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.IdValidator;

import javax.validation.ValidationException;
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

    public List<Long> addLike(String stringFilmId, String stringUserId) {
        Film film = findFilmById(stringFilmId);
        User user = userService.findUserById(stringUserId);
        film.addLike(user.getId());
        return new ArrayList<>(film.getLikes());
    }

    public List<Long> deleteLike(String stringFilmId, String stringUserId) {
        Film film = findFilmById(stringFilmId);
        User user = userService.findUserById(stringUserId);
        if (!film.deleteLike(user.getId())) {
            log.warn("Пользователь c id {} не ставил лайки фильму c id {}", stringUserId, stringFilmId);
            throw new NotFoundException(
                    String.format("Пользователь c id %s не ставил лайки фильму c id %s",
                            stringUserId, stringFilmId));
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

    public Film findFilmById(String stringId) {
        long id = IdValidator.parseId(stringId);
        return storage.findFilmById(id);
    }
}
