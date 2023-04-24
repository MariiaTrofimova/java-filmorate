package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service("DbFilmService")
public class DbFilmService implements FilmService {
    private final FilmStorage storage;
    private final LikeDao likeDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final FilmGenreDao filmGenreDao;

    @Autowired
    public DbFilmService(@Qualifier("FilmDbStorage") FilmStorage storage, LikeDao likeDao, GenreDao genreDao, MpaDao mpaDao, FilmGenreDao filmGenreDao) {
        this.storage = storage;
        this.likeDao = likeDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.filmGenreDao = filmGenreDao;
    }

    @Override
    public List<Film> listFilms() {
        return storage.listFilms().stream()
                .peek(film -> film.setMpa(mpaDao.findMpaById(film.getMpa().getId())))
                .peek(film -> filmGenreDao.getGenresByFilm(film.getId()).stream()
                        .map(genreDao::findGenreById)
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    @Override
    public Film findFilmById(long id) {
        Film film = storage.findFilmById(id);
        film.setMpa(mpaDao.findMpaById(film.getMpa().getId()));
        filmGenreDao.getGenresByFilm(film.getId()).stream()
                .map(genreDao::findGenreById)
                .forEach(film::addGenre);
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        Film filmWithId = storage.addFilm(film);
        film.getGenres().forEach(genre -> filmGenreDao.addGenreToFilm(filmWithId.getId(), genre.getId()));
        return filmWithId;
    }

    @Override
    public Film updateFilm(Film film) {
        Film filmUpdated = storage.updateFilm(film);
        filmGenreDao.clearGenresFromFilm(film.getId());
        film.getGenres().forEach(genre -> filmGenreDao.addGenreToFilm(film.getId(), genre.getId()));
        return filmUpdated;
    }

    @Override
    public List<Film> listTopFilms(Integer count) {
        return likeDao.getTopFilmId(count).stream()
                .map(storage::findFilmById)
                .peek(film -> film.setMpa(mpaDao.findMpaById(film.getMpa().getId())))
                .peek(film -> filmGenreDao.getGenresByFilm(film.getId()).stream()
                        .map(genreDao::findGenreById)
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> addLike(long filmId, long userId) {
        likeDao.addLike(filmId, userId);
        return likeDao.getLikesByFilm(filmId);
    }

    @Override
    public List<Long> deleteLike(long filmId, long userId) {
        likeDao.deleteLike(filmId, userId);
        return likeDao.getLikesByFilm(filmId);
    }
}
