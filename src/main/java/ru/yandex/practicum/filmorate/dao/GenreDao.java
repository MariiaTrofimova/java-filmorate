package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreDao {
    List<Genre> getGenres();

    Genre findGenreById(int id);

    List<Genre> getGenresByFilm(long id);

    Map<Long, Set<Genre>> getGenresByFilmList(List<Long> idList);
}
