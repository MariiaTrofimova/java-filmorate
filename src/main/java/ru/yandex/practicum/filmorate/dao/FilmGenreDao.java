package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FilmGenreDao {
    List<Integer> getGenresByFilm(long id);

    boolean addGenreToFilm(long filmId, int genreId);

    boolean deleteGenreFromFilm(long filmId, int genreId);

    boolean clearGenresFromFilm(long film_id);
}
