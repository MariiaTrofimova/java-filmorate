package ru.yandex.practicum.filmorate.storage.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository("UserDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> listUsers() {
        String sql = "select * from users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public User findUserById(long id) {
        String sql = "select * from users where user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToUser(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    @SneakyThrows
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано. Вместо него будет использован логин");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        log.debug("Пользователь {} сохранен", mapper.writeValueAsString(user));
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?;";
        if (jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()) > 0) {
            return user;
        }
        log.warn("Пользователь с id {} не найден", user.getId());
        throw new NotFoundException(String.format("Пользователь с id %d не найден", user.getId()));
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }
}