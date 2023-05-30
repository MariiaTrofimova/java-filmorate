package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> listFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(long id);

    List<Film> listTopFilms(int count);

    List<Film> listTopFilms();

    List<Film> listTopFilms(List<Long> ids);

    List<Film> listTopFilmsByYear(int count, int year);

    List<Film> listTopFilmsByYear(int year);

    boolean deleteFilm(long id);

    boolean addGenreToFilm(long filmId, int genreId);

    boolean deleteGenreFromFilm(long filmId, int genreId);

    boolean clearGenresFromFilm(long filmId);

    Map<Long, Integer> getMarksByFilm(long filmId);

    boolean addMark(long filmId, long userId, int mark);

    boolean updateMark(long filmId, long userId, int mark);

    boolean deleteMark(long filmId, long userId);

    void addDirectorToFilm(long filmId, long directorId);

    void clearDirectorsForFilm(long filmId);

    List<Long> findFilmIdsByTitleQuery(String query);

    List<Long> findFilmIdsByDirectorQuery(String query);

    List<Long> findCommonFilmIds(Long userId, Long friendId);

    Map<Long, List<Long>> getUserIdsWithMarkedFilmIdsAndMarks();
}
