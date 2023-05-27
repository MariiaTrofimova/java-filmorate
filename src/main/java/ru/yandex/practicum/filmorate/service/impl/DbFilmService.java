package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.DirectorDao;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;

@Service("DbFilmService")
public class DbFilmService implements FilmService {
    private final FilmStorage storage;
    private final DirectorDao directorDao;
    private final UserService userService;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final FeedService feedService;

    @Autowired
    public DbFilmService(@Qualifier("FilmDbStorage") FilmStorage storage,
                         DirectorDao directorDao,
                         @Qualifier("DbUserService") UserService userService,
                         GenreService genreService, DirectorService directorService, FeedService feedService) {
        this.storage = storage;
        this.directorDao = directorDao;
        this.userService = userService;
        this.genreService = genreService;
        this.directorService = directorService;
        this.feedService = feedService;
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
        List<Long> filmIds = new ArrayList<>();
        if (Arrays.asList(by).contains("director")) {
            filmIds.addAll(storage.findFilmIdsByDirectorQuery(query));
        }
        if (Arrays.asList(by).contains("title")) {
            filmIds.addAll(storage.findFilmIdsByTitleQuery(query));
        }
        filmIds = filmIds.stream().distinct().collect(Collectors.toList());
        List<Film> topFilms = storage.listTopFilms(filmIds);
        return directorService.getFilmsWithDirectors(genreService.getFilmsWithGenres(topFilms));
    }

    @Override
    public List<Film> findCommonFilms(Long userId, Long friendId) {
        userService.findUserById(userId);
        userService.findUserById(friendId);
        List<Long> filmIds = storage.findCommonFilmIds(userId, friendId);
        return storage.listTopFilms(filmIds);
    }

    @Override
    public Film findFilmById(long id) {
        Film film = storage.findFilmById(id);
        directorDao.getDirectorsByFilm(film.getId())
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
    public List<Long> addLike(long filmId, long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        //проверка на наличие лайка: в тестах два раза добавляется лайк (3, 1), и оба раза записывается feed
        List<Long> likes = storage.getLikesByFilm(filmId);
        feedService.add(filmId, userId, LIKE, ADD);
        if (likes.contains(userId)) {
            return likes;
        }
        storage.addLike(filmId, userId);
        likes.add(userId);
        return likes;
    }

    @Override
    public List<Long> deleteLike(long filmId, long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        storage.deleteLike(filmId, userId);
        feedService.add(filmId, userId, LIKE, REMOVE);
        return storage.getLikesByFilm(filmId);
    }

    @Override
    public List<Film> listFilmsByDirector(long directorId, Optional<String> sortParam) {
        Director director = directorDao.findDirectorById(directorId);
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