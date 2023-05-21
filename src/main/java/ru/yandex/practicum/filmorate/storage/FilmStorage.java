package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> listFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(long id);

    List<Film> listTopFilms(int count);

    List<Film> listTopFilms();

    List<Film> listTopFilmsByYear(int count, int year);

    List<Film> listTopFilmsByYear(int year);

    boolean addGenreToFilm(long filmId, int genreId);

    boolean deleteGenreFromFilm(long filmId, int genreId);

    boolean clearGenresFromFilm(long filmId);

    List<Long> getLikesByFilm(long filmId);

    boolean addLike(long filmId, long userId);

    boolean deleteLike(long filmId, long userId);
}
