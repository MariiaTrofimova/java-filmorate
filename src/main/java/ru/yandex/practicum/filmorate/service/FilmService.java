package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {

    List<Film> listFilms();

    Film findFilmById(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> listTopFilms(int count);

    List<Film> listTopFilms(int count, Optional<Integer> year, Optional<Integer> genreId);

    List<Film> findFilmsByQuery(String query, String[] by);

    List<Film> findCommonFilms(Long userId, Long friendId);

    boolean addMark(long filmId, long userId, int mark);

    boolean deleteMark(long filmId, long userId);

    List<Film> listFilmsByDirector(long directorId, Optional<String> sortParam);

    boolean deleteFilm(long id);
}
