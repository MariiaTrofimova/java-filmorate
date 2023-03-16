package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int nextUserId = 1;
    private final HashMap<Integer, User> users = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @GetMapping
    public List<User> listUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) throws JsonProcessingException {
        if (UserValidation.isUserValid(user, bindingResult)) {
            int id = nextUserId++;
            user.setId(id);
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(id, user);
            log.debug("Пользователь {} сохранен", mapper.writeValueAsString(user));
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult) throws JsonProcessingException {
        if (UserValidation.isUserValid(user, bindingResult)) {
            int id = user.getId();
            if (id == 0) {
                log.warn("Отсутствует id");
                throw new ValidationException("Отсутствует id");
            } else if (!users.containsKey(id)) {
                log.warn("Пользователь с id {} не найден", id);
                throw new ValidationException("Пользователь с id " + id + " не найден");
            } else {
                users.put(id, user);
                log.debug("Пользователь {} обновлен", mapper.writeValueAsString(user));
            }
        }
        return user;
    }
}
