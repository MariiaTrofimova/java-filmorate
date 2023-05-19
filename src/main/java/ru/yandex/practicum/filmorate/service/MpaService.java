package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {


    List<Mpa> listMpa();

    Mpa findMpaById(int id);
}
