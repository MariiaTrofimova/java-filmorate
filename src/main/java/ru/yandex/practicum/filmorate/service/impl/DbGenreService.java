package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DbGenreService implements GenreService {
    private final GenreDao genreDao;

    public DbGenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @Override
    public List<Genre> listGenres() {
        return genreDao.getGenres();
    }

    @Override
    public Genre findGenreById(int id) {
        return genreDao.findGenreById(id);
    }

    @Override
    public List<Film> getFilmsWithGenres(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Long, Set<Genre>> genresByFilmList = genreDao.getGenresByFilmList(filmIds);
        return films.stream()
                .peek(film -> genresByFilmList.getOrDefault(film.getId(), new HashSet<>())
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> getGenresByFilm(long id) {
        return genreDao.getGenresByFilm(id);
    }
}
