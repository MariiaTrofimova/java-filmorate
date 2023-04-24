package ru.yandex.practicum.filmorate.service.impl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

public class DbFilmService implements FilmService {
    @Override
    public List<Film> listFilms() {
        //присунуть фильмам mpa и жанры
        return null;
    }

    @Override
    public Film findFilmById(long id) {
        //присунуть фильму mpa и жанры
        return null;
    }

    @Override
    public Film addFilm(Film film) {
        //раскидать mpa и жанры
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        //присунуть фильму mpa и жанры
        return null;
    }

    @Override
    public List<Film> listTopFilms(Integer count) {
        //присунуть фильмам mpa и жанры
        return null;
    }

    @Override
    public List<Long> addLike(long filmId, long userId) {
        return null;
    }

    @Override
    public List<Long> deleteLike(long filmId, long userId) {
        return null;
    }
}
