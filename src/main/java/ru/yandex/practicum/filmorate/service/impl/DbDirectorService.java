package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.List;

@Service("DbDirectorService")
public class DbDirectorService implements DirectorService {

    private final DirectorDao directorDao;

    public DbDirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    @Override
    public Director findDirectorById(long id) {
        return directorDao.findDirectorById(id);
    }

    @Override
    public List<Director> listDirectors() {
        return directorDao.listDirectors();
    }

    @Override
    public Director addDirector(Director director) {
        return directorDao.addDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }

    @Override
    public boolean deleteDirector(long id) {
        return directorDao.deleteDirector(id);
    }
}
