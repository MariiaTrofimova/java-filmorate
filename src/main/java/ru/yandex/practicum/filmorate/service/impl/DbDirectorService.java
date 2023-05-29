package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("DbDirectorService")
public class DbDirectorService implements DirectorService {

    private final DirectorStorage directorStorage;

    public DbDirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Director findDirectorById(long id) {
        return directorStorage.findDirectorById(id);
    }

    @Override
    public List<Director> listDirectors() {
        return directorStorage.listDirectors();
    }

    @Override
    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    @Override
    public boolean deleteDirector(long id) {
        return directorStorage.deleteDirector(id);
    }

    @Override
    public List<Film> getFilmsWithDirectors(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Long, Set<Director>> directorsByFilmList = directorStorage.getDirectorsByFilmList(filmIds);
        return films.stream()
                .peek(film -> directorsByFilmList.getOrDefault(film.getId(), new HashSet<>())
                        .forEach(film::addDirector))
                .collect(Collectors.toList());
    }
}
