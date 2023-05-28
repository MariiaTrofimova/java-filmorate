package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpas() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs));
    }

    @Override
    public Mpa findMpaById(int id) {
        String sql = "select * from mpa where mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToMpa(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Mpa с id {} не найден", id);
            throw new NotFoundException(String.format("Mpa с id %d не найден", id));
        }
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
