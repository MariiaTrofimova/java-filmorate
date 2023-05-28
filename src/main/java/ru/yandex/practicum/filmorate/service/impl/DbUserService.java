package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
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
    private final FriendshipStorage friendshipStorage;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final FeedService feedService;


    public DbUserService(@Qualifier("UserDbStorage") UserStorage storage,
                         FriendshipStorage friendshipStorage, FilmStorage filmStorage, GenreService genreService, FeedService feedService) {
        this.storage = storage;
        this.friendshipStorage = friendshipStorage;
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
        return friendshipStorage.getFriendsByUser(id).stream()
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> listCommonFriends(long id, long otherId) {
        findUserById(id);
        findUserById(otherId);
        return friendshipStorage.getFriendsByUser(id).stream()
                .filter(friendshipStorage.getFriendsByUser(otherId)::contains)
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> addFriend(long id, long friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean isUserToFriend = friendshipStorage.getFriendsByUser(id).contains(friendId);
        boolean isFriendToUser = friendshipStorage.getFriendsByUser(friendId).contains(id);
        if (!isUserToFriend && !isFriendToUser) {
            friendshipStorage.addFriend(id, friendId);
            feedService.add(friendId, id, FRIEND, ADD);
        } else if (isUserToFriend && !isFriendToUser) {
            friendshipStorage.updateFriend(friendId, id, true);
            feedService.add(friendId, id, FRIEND, UPDATE);
        } else {
            log.debug("Повторный запрос в друзья от пользователя с id {} пользователю с id {}", id, friendId);
        }
        return friendshipStorage.getFriendsByUser(id);
    }

    @Override
    public List<Long> deleteFriend(long id, long friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean isUserHasFriend = friendshipStorage.getFriendsByUser(id).contains(friendId);
        boolean isFriendHasUser = friendshipStorage.getFriendsByUser(friendId).contains(id);
        if (!isUserHasFriend) {
            log.warn("Пользователь c id {} не является другом пользователя c id {}", friendId, id);
            throw new NotFoundException(
                    String.format("Пользователь c id %d не является другом пользователя c id %d",
                            friendId, id));
        } else if (!isFriendHasUser) {
            friendshipStorage.deleteFriend(friendId, id);
            feedService.add(friendId, id, FRIEND, REMOVE);
        } else {
            if (!friendshipStorage.updateFriend(id, friendId, false)) {
                friendshipStorage.deleteFriend(friendId, id);
                friendshipStorage.addFriend(id, friendId);
                feedService.add(friendId, id, FRIEND, REMOVE);
            }
        }
        return friendshipStorage.getFriendsByUser(id);
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
        return genreService.getFilmsWithGenres(
                filmStorage.listTopFilms(filmIds));

    }

    @Override
    public List<Feed> getFeedByUserId(long id) {
        storage.findUserById(id);
        return feedService.getByUserId(id);
    }
}
