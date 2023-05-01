package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> getGenres();

    Genre findGenreById(int id);

    List<Genre> getGenresByFilm(long id);
}
