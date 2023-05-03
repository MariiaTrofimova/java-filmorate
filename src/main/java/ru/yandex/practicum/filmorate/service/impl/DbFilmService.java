package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service("DbFilmService")
public class DbFilmService implements FilmService {
    private final FilmStorage storage;
    private final GenreDao genreDao;
    private final UserService userService;

    @Autowired
    public DbFilmService(@Qualifier("FilmDbStorage") FilmStorage storage,
                         GenreDao genreDao,
                         @Qualifier("DbUserService") UserService userService) {
        this.storage = storage;
        this.genreDao = genreDao;
        this.userService = userService;
    }

    @Override
    public List<Film> listFilms() {
        List<Film> films = storage.listFilms();
        return getFilmsWithGenres(films);
    }

    @Override
    public List<Film> listTopFilms(Integer count) {
        List<Film> topFilms = storage.listTopFilms(count);
        return getFilmsWithGenres(topFilms);
    }

    @Override
    public Film findFilmById(long id) {
        Film film = storage.findFilmById(id);
        genreDao.getGenresByFilm(film.getId())
                .forEach(film::addGenre);
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    @Override
    public List<Long> addLike(long filmId, long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        storage.addLike(filmId, userId);
        return storage.getLikesByFilm(filmId);
    }

    @Override
    public List<Long> deleteLike(long filmId, long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        storage.deleteLike(filmId, userId);
        return storage.getLikesByFilm(filmId);
    }

    private List<Film> getFilmsWithGenres(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Long, Set<Genre>> genresByFilmList = genreDao.getGenresByFilmList(filmIds);
        return films.stream()
                .peek(film -> genresByFilmList.getOrDefault(film.getId(), new HashSet<>())
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }
}
