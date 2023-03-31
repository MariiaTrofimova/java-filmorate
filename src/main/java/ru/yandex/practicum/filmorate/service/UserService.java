package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public List<User> listFriends(long id) {
        User user = findUserById(id);
        log.debug("Текущее количество друзей у пользователя с id {}: {}", id, user.getFriends().size());
        return user.getFriends().stream()
                .map(storage::findUserById)
                .collect(Collectors.toList());
    }

    public List<Long> addFriend(long id, long friendId) {
        User user = findUserById(id);
        User userFriend = findUserById(friendId);
        user.addFriend(userFriend.getId());
        userFriend.addFriend(user.getId());
        log.debug("Текущее количество друзей у пользователя с id {}: {}", id, user.getFriends().size());
        return new ArrayList<>(user.getFriends());
    }

    public List<Long> deleteFriend(long id, long friendId) {
        User user = findUserById(id);
        User userFriend = findUserById(friendId);

        if (!user.deleteFriend(friendId)) {
            log.warn("Пользователь c id {} не является другом пользователя c id {}", id, friendId);
            throw new NotFoundException(
                    String.format("Пользователь c id %d не является другом пользователя c id %d",
                            id, friendId));
        }
        if (!userFriend.deleteFriend(id)) {
            log.debug("Пользователь c id {} удалил пользователя c id {} ранее", friendId, id);
        }
        log.debug("Текущее количество друзей у пользователя с id {}: {}", id, user.getFriends().size());
        return new ArrayList<>(user.getFriends());
    }

    public List<User> listCommonFriends(long id, long otherId) {
        User user = findUserById(id);
        User otherUser = findUserById(otherId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(storage::findUserById)
                .collect(Collectors.toList());
    }

    public List<User> listUsers() {
        return storage.listUsers();
    }

    public User addUser(User user) {
        return storage.addUser(user);
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    public User findUserById(long id) {
        return storage.findUserById(id);
    }
}
