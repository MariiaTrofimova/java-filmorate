package ru.yandex.practicum.filmorate.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long nextUserId = 1;
    private final HashMap<Long, User> users = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public List<User> listUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        long id = nextUserId++;
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано. Вместо него будет использован логин");
        }
        users.put(id, user);
        try {
            log.debug("Пользователь {} сохранен", mapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User updateUser(User user) {
        long id = user.getId();
        if (id == 0) {
            log.warn("Отсутствует id");
            throw new NotFoundException("Отсутствует id");
        } else if (!users.containsKey(id)) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        } else {
            users.put(id, user);
            try {
                log.debug("Пользователь {} обновлен", mapper.writeValueAsString(user));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return user;
    }
}
