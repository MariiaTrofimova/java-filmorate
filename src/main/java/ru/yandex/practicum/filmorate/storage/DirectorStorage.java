package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorStorage {
    Map<Long, Set<Director>> getDirectorsByFilmList(List<Long> idList);

    Director findDirectorById(long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    List<Director> listDirectors();

    boolean deleteDirector(long id);

    List<Director> getDirectorsByFilm(long id);
}
