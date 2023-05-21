package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> listUsers();

    User addUser(User user);

    User updateUser(User user);

    User findUserById(long id);

    boolean processEvent(Event event);
}
