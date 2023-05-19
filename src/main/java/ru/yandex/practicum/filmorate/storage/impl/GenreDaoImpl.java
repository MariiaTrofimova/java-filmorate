package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "select * from genre order by genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToGenre(rs));
    }

    @Override
    public Genre findGenreById(int id) {
        String sql = "select * from genre where genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToGenre(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Жанр с id {} не найден", id);
            throw new NotFoundException(String.format("Жанр с id %d не найден", id));
        }
    }

    @Override
    public List<Genre> getGenresByFilm(long id) {
        String sql = "select g.* from film_genre as fg " +
                "join genre as g on fg.genre_id = g.genre_id " +
                "where fg.film_id =? " +
                "order by g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToGenre(rs), id);
    }

    @Override
    public Map<Long, Set<Genre>> getGenresByFilmList(List<Long> ids) {
        String sql = "select fg.FILM_ID, fg.GENRE_ID, g.name " +
                "from film_genre as fg join genre as g on fg.genre_id = g.genre_id " +
                "where fg.film_id in (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        final Map<Long, Set<Genre>> genresByFilmList = new HashMap<>();

        namedJdbcTemplate.query(sql, parameters,
                rs -> {
                    long filmId = rs.getLong("film_id");
                    Genre genre = mapRowToGenre(rs);
                    Set<Genre> genres = genresByFilmList.getOrDefault(filmId, new HashSet<>());
                    genres.add(genre);
                    genresByFilmList.put(filmId, genres);
                });
        return genresByFilmList;
    }

    private Genre mapRowToGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }
}
