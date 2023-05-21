package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    Director findDirectorById(long id);

    List<Director> listDirectors();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirector(long id);
}
