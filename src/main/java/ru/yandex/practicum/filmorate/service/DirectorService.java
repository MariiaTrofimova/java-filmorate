package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorService {

    Director findDirectorById(long id);

    List<Director> listDirectors();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirector(long id);

    List<Film> getFilmsWithDirectors(List<Film> films);
}
