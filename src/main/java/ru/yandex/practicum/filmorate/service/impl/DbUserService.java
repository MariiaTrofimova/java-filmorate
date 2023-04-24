package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Service("DbUserService")
public class DbUserService implements UserService {
    @Override
    public List<User> listUsers() {
        return null;
    }

    @Override
    public User findUserById(long id) {
        return null;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public List<User> listFriends(long id) {
        return null;
    }

    @Override
    public List<User> listCommonFriends(long id, long otherId) {
        return null;
    }

    @Override
    public List<Long> addFriend(long id, long friendId) {
        return null;
    }

    @Override
    public List<Long> deleteFriend(long id, long friendId) {
        return null;
    }
}
