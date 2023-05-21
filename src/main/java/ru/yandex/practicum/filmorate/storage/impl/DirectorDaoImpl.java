package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
public class DirectorDaoImpl implements DirectorDao {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public DirectorDaoImpl(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
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
                    Set<Director> directors = directorsByFilmList.getOrDefault(filmId, new HashSet<>());
                    directors.add(director);
                    directorsByFilmList.put(filmId, directors);
                });
        return directorsByFilmList;
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
