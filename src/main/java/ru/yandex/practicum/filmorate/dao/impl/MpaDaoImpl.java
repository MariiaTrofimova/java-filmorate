package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpas() {
        String sql = "select * from mpa;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs));
    }

    @Override
    public Mpa findMpaById(int id) {
        String sql = "select * from mpa where mpa_id = ?";
        Optional<Mpa> mpaOptional = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs)).stream().findAny();
        if (mpaOptional.isPresent()) {
            return mpaOptional.get();
        }
        log.warn("Рейтинг MPA с id {} не найден", id);
        throw new NotFoundException(String.format("Рейтинг MPA с id %d не найден", id));
    }

    private Mpa mapRowToMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        return Mpa.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}
