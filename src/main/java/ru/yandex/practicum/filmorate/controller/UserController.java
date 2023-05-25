package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(@Qualifier("DbUserService") UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> listUsers() {
        return service.listUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable long id) {
        return service.findUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> listFriends(@PathVariable long id) {
        return service.listFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> listCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return service.listCommonFriends(id, otherId);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable long id) {
        return service.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<Long> addFriend(@PathVariable long id, @PathVariable long friendId) {
        return service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<Long> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        return service.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> recommendations(@PathVariable long id) {
        return service.recommendations(id);
    }
}
