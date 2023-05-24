package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("DbUserService")
@Slf4j
public class DbUserService implements UserService {
    private final UserStorage storage;
    private final FilmStorage filmStorage;
    private final FriendshipDao friendshipDao;

    public DbUserService(@Qualifier("UserDbStorage") UserStorage storage, FilmStorage filmStorage, FriendshipDao friendshipDao) {
        this.storage = storage;
        this.filmStorage = filmStorage;
        this.friendshipDao = friendshipDao;
    }

    @Override
    public List<User> listUsers() {
        return storage.listUsers();
    }

    @Override
    public User findUserById(long id) {
        return storage.findUserById(id);
    }

    @Override
    public User addUser(User user) {
        return storage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    @Override
    public boolean deleteUser(long id) {
        return storage.deleteUser(id);
    }

    public List<Film> recommendations(long userId) {
        List<Film> likeFilm = filmStorage. getFilmsWithLikes();
        Map<Long, List<Film>> likeAndUser = new HashMap<>();
        Map<Long, Long> matchesLike = new HashMap<>();
        int maxMatches = 0;
        List<Film> likeUser;
        Long id = null;
        for (Film film : likeFilm) {
            for (Long user : film.getLikes()) {
                if (likeAndUser.containsKey(user)) {
                    likeAndUser.get(user).add(film);
                } else {
                    List<Film> likedFilms = new ArrayList<>();
                    likedFilms.add(film);
                    likeAndUser.put(user, likedFilms);
                }
            }
        }
        likeUser = likeAndUser.get(userId);

        for (Map.Entry<Long, List<Film>> map : likeAndUser.entrySet()) {
            if (matchesLike.containsKey(map.getKey())) {
                matchesLike.put(map.getKey(), null);
            }
            Long matches = matchesLike.get(map.getKey());
            likeUser.stream()
                    .filter(film -> map.getValue().contains(film))
                    .forEach(film -> matchesLike.put(map.getKey(), matches + 1));
        }
        for (Map.Entry<Long, Long> map : matchesLike.entrySet()) {
            if (maxMatches < map.getValue()) {
                maxMatches = Math.toIntExact(map.getValue());
                id = map.getKey();
            }
            if (maxMatches == 0) {
                return new ArrayList<>();
            }
        }
        return likeAndUser.get(id).stream()
                .filter(film -> !likeUser.contains(film))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> listFriends(long id) {
        findUserById(id);
        return friendshipDao.getFriendsByUser(id).stream()
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> listCommonFriends(long id, long otherId) {
        findUserById(id);
        findUserById(otherId);
        return friendshipDao.getFriendsByUser(id).stream()
                .filter(friendshipDao.getFriendsByUser(otherId)::contains)
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> addFriend(long id, long friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean isUserToFriend = friendshipDao.getFriendsByUser(id).contains(friendId);
        boolean isFriendToUser = friendshipDao.getFriendsByUser(friendId).contains(id);
        if (!isUserToFriend && !isFriendToUser) {
            friendshipDao.addFriend(id, friendId);
        } else if (isUserToFriend && !isFriendToUser) {
            friendshipDao.updateFriend(friendId, id, true);
        } else {
            log.debug("Повторный запрос в друзья от пользователя с id {} пользователю с id {}", id, friendId);
        }
        return friendshipDao.getFriendsByUser(id);
    }

    @Override
    public List<Long> deleteFriend(long id, long friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean isUserHasFriend = friendshipDao.getFriendsByUser(id).contains(friendId);
        boolean isFriendHasUser = friendshipDao.getFriendsByUser(friendId).contains(id);
        if (!isUserHasFriend) {
            log.warn("Пользователь c id {} не является другом пользователя c id {}", friendId, id);
            throw new NotFoundException(
                    String.format("Пользователь c id %d не является другом пользователя c id %d",
                            friendId, id));
        } else if (!isFriendHasUser) {
            friendshipDao.deleteFriend(friendId, id);
        } else {
            if (!friendshipDao.updateFriend(id, friendId, false)) {
                friendshipDao.deleteFriend(friendId, id);
                friendshipDao.addFriend(id, friendId);
            }
        }
        return friendshipDao.getFriendsByUser(id);
    }
}
