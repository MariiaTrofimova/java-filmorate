package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class DbMpaService implements MpaService {
    private final MpaStorage mpaStorage;

    public DbMpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> listMpa() {
        return mpaStorage.getMpas();
    }

    @Override
    public Mpa findMpaById(int id) {
        return mpaStorage.findMpaById(id);
    }
}
