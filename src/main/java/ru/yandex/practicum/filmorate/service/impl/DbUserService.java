package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service("DbUserService")
@Slf4j
public class DbUserService implements UserService {
    private final UserStorage storage;
    private final FriendshipDao friendshipDao;

    public DbUserService(@Qualifier("UserDbStorage") UserStorage storage, FriendshipDao friendshipDao) {
        this.storage = storage;
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
    public List<User> listFriends(long id) {
        return friendshipDao.getFriendsByUser(id).stream()
                .map(storage::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> listCommonFriends(long id, long otherId) {
        return friendshipDao.getFriendsByUser(id).stream()
                .filter(friendshipDao.getFriendsByUser(otherId)::contains)
                .map(storage::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> addFriend(long id, long friendId) {
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
        boolean isUserToFriend = friendshipDao.getFriendsByUser(id).contains(friendId);
        boolean isFriendToUser = friendshipDao.getFriendsByUser(friendId).contains(id);
        if (!isUserToFriend && !isFriendToUser){
            log.warn("Пользователь c id {} не является другом пользователя c id {}", id, friendId);
            throw new NotFoundException(
                    String.format("Пользователь c id %d не является другом пользователя c id %d",
                            id, friendId));
        } else if (!isUserToFriend) {
            friendshipDao.deleteFriend(id, friendId);
        } else if (!isFriendToUser) {
            friendshipDao.updateFriend(friendId, id, false);
        } else {
            if (!friendshipDao.updateFriend(friendId, id, false)) {
                friendshipDao.deleteFriend(id, friendId);
                friendshipDao.addFriend(friendId, id);
            }
        }
        return friendshipDao.getFriendsByUser(id);
    }
}
