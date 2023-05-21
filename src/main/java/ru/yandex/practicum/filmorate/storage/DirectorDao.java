package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorDao {
    Map<Long, Set<Director>> getDirectorsByFilmList(List<Long> idList);
}
