package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.enums.Operation.*;

@Service("DbUserService")
@Slf4j
public class DbUserService implements UserService {
    private final UserStorage storage;
    private final FriendshipDao friendshipDao;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final FeedService feedService;


    public DbUserService(@Qualifier("UserDbStorage") UserStorage storage,
                         FriendshipDao friendshipDao, FilmStorage filmStorage, GenreService genreService,FeedService feedService) {
        this.storage = storage;
        this.friendshipDao = friendshipDao;
        this.feedService = feedService;
        this.filmStorage = filmStorage;
        this.genreService = genreService;
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
            feedService.add(friendId, id, FRIEND, ADD);
        } else if (isUserToFriend && !isFriendToUser) {
            friendshipDao.updateFriend(friendId, id, true);
            feedService.add(friendId, id, FRIEND, UPDATE);
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
            feedService.add(friendId, id, FRIEND, REMOVE);
        } else {
            if (!friendshipDao.updateFriend(id, friendId, false)) {
                friendshipDao.deleteFriend(friendId, id);
                friendshipDao.addFriend(id, friendId);
                feedService.add(friendId, id, FRIEND, REMOVE);
            }
        }
        return friendshipDao.getFriendsByUser(id);
    }

    @Override
    public List<Film> recommendations(long userId) {
        Map<Long, List<Long>> usersLikes = filmStorage.getUserIdsLikedFilmIds();
        if (usersLikes.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> userLikes = usersLikes.getOrDefault(userId, Collections.emptyList());
        usersLikes.remove(userId);

        long matchesMax = 0;
        long userIdMax = -1L;
        for (Map.Entry<Long, List<Long>> userOtherLikes : usersLikes.entrySet()) {
            long matches = userOtherLikes.getValue().stream()
                    .filter(userLikes::contains)
                    .count();
            if (matches > matchesMax) {
                matchesMax = matches;
                userIdMax = userOtherLikes.getKey();
            }
        }
        if (matchesMax == 0) {
            return Collections.emptyList();
        }

        List<Long> filmIds = usersLikes.getOrDefault(userIdMax, Collections.emptyList()).stream()
                .filter(film -> !userLikes.contains(film))
                .collect(Collectors.toList());
        //return filmStorage.listTopFilms(filmIds);
        return genreService.getFilmsWithGenres(
                filmStorage.listTopFilms(filmIds));
    }

    @Override
    public List<Feed> getFeedByUserId(long id) {
        return feedService.getByUserId(id);
    }
}
