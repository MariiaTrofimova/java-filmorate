package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DbGenreService implements GenreService {
    private final GenreStorage genreStorage;

    public DbGenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> listGenres() {
        return genreStorage.getGenres();
    }

    @Override
    public Genre findGenreById(int id) {
        return genreStorage.findGenreById(id);
    }

    @Override
    public List<Film> getFilmsWithGenres(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Long, Set<Genre>> genresByFilmList = genreStorage.getGenresByFilmList(filmIds);
        return films.stream()
                .peek(film -> genresByFilmList.getOrDefault(film.getId(), new HashSet<>())
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> getGenresByFilm(long id) {
        return genreStorage.getGenresByFilm(id);
    }
}
