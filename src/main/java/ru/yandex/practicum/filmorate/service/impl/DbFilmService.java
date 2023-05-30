package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.enums.EventType.MARK;
import static ru.yandex.practicum.filmorate.model.enums.Operation.*;

@Service("DbFilmService")
public class DbFilmService implements FilmService {
    private final FilmStorage storage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final FeedStorage feedStorage;

    @Autowired
    public DbFilmService(@Qualifier("FilmDbStorage") FilmStorage storage,
                         DirectorStorage directorStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         GenreService genreService, DirectorService directorService, FeedStorage feedStorage) {
        this.storage = storage;
        this.directorStorage = directorStorage;
        this.userStorage = userStorage;
        this.genreService = genreService;
        this.directorService = directorService;
        this.feedStorage = feedStorage;
    }

    @Override
    public List<Film> listFilms() {
        List<Film> films = storage.listFilms();
        return directorService.getFilmsWithDirectors(genreService.getFilmsWithGenres(films));
    }

    @Override
    public List<Film> listTopFilms(int count) {
        List<Film> topFilms = storage.listTopFilms(count);
        topFilms = genreService.getFilmsWithGenres(topFilms);
        return directorService.getFilmsWithDirectors(topFilms);
    }

    @Override
    public List<Film> listTopFilms(int count, Optional<Integer> year, Optional<Integer> genreId) {
        List<Film> topFilms;
        if (year.isEmpty() && genreId.isEmpty()) {
            return listTopFilms(count);
        }

        if (year.isPresent()) {
            if (genreId.isEmpty()) {
                topFilms = storage.listTopFilmsByYear(count, year.get());
                topFilms = genreService.getFilmsWithGenres(topFilms);
            } else {
                topFilms = storage.listTopFilmsByYear(year.get());
            }
        } else {
            topFilms = storage.listTopFilms();
        }

        if (genreId.isPresent()) {
            Genre genre = genreService.findGenreById(genreId.get());
            topFilms = genreService.getFilmsWithGenres(topFilms).stream()
                    .filter(film -> film.getGenres().contains(genre))
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return directorService.getFilmsWithDirectors(topFilms);
    }

    @Override
    public List<Film> findFilmsByQuery(String query, String[] by) {
        Set<Long> filmIds = new HashSet<>();
        Collection<String> strings = new HashSet<>(Arrays.asList(by));
        if (strings.contains("director")) {
            filmIds.addAll(storage.findFilmIdsByDirectorQuery(query));
        }
        if (strings.contains("title")) {
            filmIds.addAll(storage.findFilmIdsByTitleQuery(query));
        }
        List<Film> topFilms = storage.listTopFilms(new ArrayList<>(filmIds));
        return directorService.getFilmsWithDirectors(genreService.getFilmsWithGenres(topFilms));
    }

    @Override
    public List<Film> findCommonFilms(Long userId, Long friendId) {
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);
        List<Long> filmIds = storage.findCommonFilmIds(userId, friendId);
        return directorService.getFilmsWithDirectors(genreService.getFilmsWithGenres(storage.listTopFilms(filmIds)));
    }

    @Override
    public Film findFilmById(long id) {
        Film film = storage.findFilmById(id);
        directorStorage.getDirectorsByFilm(film.getId())
                .forEach(film::addDirector);
        genreService.getGenresByFilm(film.getId())
                .forEach(film::addGenre);
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    @Override
    public void addMark(long filmId, long userId, byte mark) {
        findFilmById(filmId);
        userStorage.findUserById(userId);
        if (storage.updateMark(filmId, userId, mark)) {
            feedStorage.addFeed(filmId, userId, MARK, UPDATE);
        } else if (storage.addMark(filmId, userId, mark)) {
            feedStorage.addFeed(filmId, userId, MARK, ADD);
        }
    }

    @Override
    public boolean deleteMark(long filmId, long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId);
        if (storage.deleteMark(filmId, userId)) {
            feedStorage.addFeed(filmId, userId, MARK, REMOVE);
            return true;
        }
        return false;
    }

    @Override
    public List<Film> listFilmsByDirector(long directorId, Optional<String> sortParam) {
        Director director = directorStorage.findDirectorById(directorId);
        if (sortParam.isPresent()) {
            if (sortParam.get().equals("year")) {
                return directorService.getFilmsWithDirectors(listFilms())
                        .stream()
                        .filter(film -> film.getDirectors().contains(director))
                        .sorted(Comparator.comparing((Film film) -> film.getReleaseDate().getYear()))
                        .collect(Collectors.toList());
            } else if (sortParam.get().equals("likes")) {
                List<Film> topFilms = genreService.getFilmsWithGenres(storage.listTopFilms());
                return directorService.getFilmsWithDirectors(topFilms)
                        .stream()
                        .filter(film -> film.getDirectors().contains(director))
                        .collect(Collectors.toList());
            }
        }
        return directorService.getFilmsWithDirectors(listFilms())
                .stream()
                .filter(film -> film.getDirectors().contains(director))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFilm(long id) {
        return storage.deleteFilm(id);
    }
}