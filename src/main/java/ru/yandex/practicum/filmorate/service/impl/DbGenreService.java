package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

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
}
