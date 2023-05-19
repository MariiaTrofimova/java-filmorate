package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreDao genreDao;

    @Autowired
    public GenreController(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @GetMapping
    public List<Genre> listGenres() {
        return genreDao.getGenres();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable int id) {
        return genreDao.findGenreById(id);
    }
}
