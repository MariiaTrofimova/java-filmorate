package ru.yandex.practicum.filmorate.storage.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public DirectorDbStorage(NamedParameterJdbcTemplate namedJdbcTemplate,
                             JdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Set<Director>> getDirectorsByFilmList(List<Long> ids) {
        String sql = "select fd.FILM_ID, fd.DIRECTOR_ID, d.name " +
                "from film_director as fd join directors as d on fd.director_id = d.director_id " +
                "where fd.film_id in (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        final Map<Long, Set<Director>> directorsByFilmList = new HashMap<>();

        namedJdbcTemplate.query(sql, parameters,
                rs -> {
                    long filmId = rs.getLong("film_id");
                    Director director = mapRowToDirector(rs);
                    directorsByFilmList.computeIfAbsent(filmId, k -> new HashSet<>()).add(director);
                });
        return directorsByFilmList;
    }

    @Override
    public List<Director> getDirectorsByFilm(long id) {
        String sql = "select d.* from film_director as fd " +
                "join directors as d on fd.director_id = d.director_id " +
                "where fd.film_id = ? " +
                "order by d.director_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToDirector(rs), id);
    }

    @Override
    public Director findDirectorById(long id) {
        String sql = "select d.* " +
                "from directors as d " +
                "where d.director_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToDirector(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Режиссёр с id {} не найден.", id);
            throw new NotFoundException(String.format("Режиссёр с id %d не найден.", id));
        }
    }

    @Override
    @SneakyThrows
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        long id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        director.setId(id);
        log.debug("Режиссёр {} сохранен", mapper.writeValueAsString(director));
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "update directors set name = ? " +
                "where director_id = ?";
        if (jdbcTemplate.update(sql, director.getName(), director.getId()) > 0) {
            return director;
        }
        log.warn("Режиссёр с id {} не найден", director.getId());
        throw new NotFoundException(String.format("Режиссёр с id %d не найден", director.getId()));
    }

    @Override
    public List<Director> listDirectors() {
        String sql = "select * from directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToDirector(rs));
    }

    @Override
    public boolean deleteDirector(long id) {
        String sql = "delete from directors where director_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private Director mapRowToDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("director_id");
        String name = rs.getString("name");
        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }
}
