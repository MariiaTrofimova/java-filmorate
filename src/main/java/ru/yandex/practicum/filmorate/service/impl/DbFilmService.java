package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.DirectorDao;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;

@Service("DbFilmService")
public class DbFilmService implements FilmService {
    private final FilmStorage storage;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;
    private final UserService userService;
    private final FeedService feedService;

    @Autowired
    public DbFilmService(@Qualifier("FilmDbStorage") FilmStorage storage, GenreDao genreDao, DirectorDao directorDao, @Qualifier("DbUserService") UserService userService, FeedService feedService) {
        this.storage = storage;
        this.genreDao = genreDao;
        this.directorDao = directorDao;
        this.userService = userService;
        this.feedService = feedService;
    }

    @Override
    public List<Film> listFilms() {
        List<Film> films = storage.listFilms();
        return getFilmsWithDirectors(getFilmsWithGenres(films));
    }

    @Override
    public List<Film> listTopFilms(int count) {
        List<Film> topFilms = storage.listTopFilms(count);
        topFilms = getFilmsWithGenres(topFilms);
        return getFilmsWithDirectors(topFilms);
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
                topFilms = getFilmsWithGenres(topFilms);
            } else {
                topFilms = storage.listTopFilmsByYear(year.get());
            }
        } else {
            topFilms = storage.listTopFilms();
        }

        if (genreId.isPresent()) {
            Genre genre = genreDao.findGenreById(genreId.get());
            topFilms = getFilmsWithGenres(topFilms).stream()
                    .filter(film -> film.getGenres().contains(genre))
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return getFilmsWithDirectors(topFilms);
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
        return getFilmsWithDirectors(getFilmsWithGenres(topFilms));
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
        genreDao.getGenresByFilm(film.getId())
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
        storage.addLike(filmId, userId);
        feedService.add(filmId, userId, LIKE, ADD);
        return storage.getLikesByFilm(filmId);
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
                return getFilmsWithDirectors(listFilms())
                        .stream()
                        .filter(film -> film.getDirectors().contains(director))
                        .sorted(Comparator.comparing((Film film) -> film.getReleaseDate().getYear()))
                        .collect(Collectors.toList());
            } else if (sortParam.get().equals("likes")) {
                List<Film> topFilms = getFilmsWithGenres(storage.listTopFilms());
                return getFilmsWithDirectors(topFilms)
                        .stream()
                        .filter(film -> film.getDirectors().contains(director))
                        .collect(Collectors.toList());
            }
        }
        return getFilmsWithDirectors(listFilms())
                .stream()
                .filter(film -> film.getDirectors().contains(director))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFilm(long id) {
        return storage.deleteFilm(id);
    }

    private List<Film> getFilmsWithGenres(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Long, Set<Genre>> genresByFilmList = genreDao.getGenresByFilmList(filmIds);
        return films.stream()
                .peek(film -> genresByFilmList.getOrDefault(film.getId(), new HashSet<>())
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    private List<Film> getFilmsWithDirectors(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Long, Set<Director>> directorsByFilmList = directorDao.getDirectorsByFilmList(filmIds);
        return films.stream()
                .peek(film -> directorsByFilmList.getOrDefault(film.getId(), new HashSet<>())
                        .forEach(film::addDirector))
                .collect(Collectors.toList());
    }
}
