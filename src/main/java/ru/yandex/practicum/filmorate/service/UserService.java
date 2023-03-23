package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private int nextUserId = 1;
    private final HashMap<Integer, User> users = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public List<User> listUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public User addUser(User user) throws JsonProcessingException {
        int id = nextUserId++;
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано. Вместо него будет использован логин");
        }
        users.put(id, user);
        log.debug("Пользователь {} сохранен", mapper.writeValueAsString(user));
        return user;
    }

    public User updateUser(User user) throws JsonProcessingException {
        int id = user.getId();
        if (id == 0) {
            log.warn("Отсутствует id");
            throw new NotFoundException("Отсутствует id");
        } else if (!users.containsKey(id)) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        } else {
            users.put(id, user);
            log.debug("Пользователь {} обновлен", mapper.writeValueAsString(user));
        }
        return user;
    }
}
