package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaDao;

import java.util.List;

@Service
public class DbMpaService implements MpaService {
    private final MpaDao mpaDao;

    public DbMpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Override
    public List<Mpa> listMpa() {
        return mpaDao.getMpas();
    }

    @Override
    public Mpa findMpaById(int id) {
        return mpaDao.findMpaById(id);
    }
}
