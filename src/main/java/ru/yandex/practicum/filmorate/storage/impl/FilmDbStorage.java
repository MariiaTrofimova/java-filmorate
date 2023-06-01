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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public List<Film> listFilms() {
        String sql = "select f.*, m.name as mpa_name from films as f join mpa as m on f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Film findFilmById(long id) {
        String sql = "select f.*, m.name as mpa_name " +
                "from films as f " +
                "join mpa as m on f.mpa_id = m.mpa_id " +
                "where f.film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToFilm(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Фильм с id {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id %d не найден", id));
        }
    }

    @Override
    @SneakyThrows
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        film.getGenres().forEach(genre -> addGenreToFilm(id, genre.getId()));
        film.getDirectors().forEach(director -> addDirectorToFilm(id, director.getId()));
        log.debug("Фильм {} сохранен", mapper.writeValueAsString(film));
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        String sql = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "where film_id = ?";
        if (jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) > 0) {
            clearGenresFromFilm(film.getId());
            clearDirectorsForFilm(film.getId());
            film.getGenres().forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
            film.getDirectors().forEach(director -> addDirectorToFilm(film.getId(), director.getId()));
            return film;
        }
        log.warn("Фильм с id {} не найден", film.getId());
        throw new NotFoundException(String.format("Фильм с id %d не найден", film.getId()));
    }

    @Override
    public List<Film> listTopFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, AVG(mk.mark) AS mark_rating " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN marks AS mk ON f.film_id = mk.film_id " +
                "GROUP BY f.film_id " +
                //"HAVING AVG(mk.mark) > 5 " +
                "ORDER BY mark_rating DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), count, count);
    }


    @Override
    public List<Film> listTopFilms() {
        String sql = "SELECT f.*, m.name AS mpa_name, AVG(mk.mark) AS mark_rating " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN marks AS mk ON f.film_id = mk.film_id " +
                "GROUP BY f.film_id " +
                //"HAVING AVG(mk.mark) > 5 " +
                "ORDER BY mark_rating DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public List<Film> listTopFilms(List<Long> ids) {
        String sql = "SELECT f.*, m.name AS mpa_name, AVG(mk.mark) AS mark_rating " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN marks AS mk ON f.film_id = mk.film_id " +
                "WHERE f.film_id IN (:ids) " +
                "GROUP BY f.film_id " +
                //"HAVING AVG(mk.mark) > 5 " +
                "ORDER BY mark_rating DESC";

        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public List<Film> listTopFilmsByYear(int count, int year) {
        String sql = "SELECT f.*, m.name AS mpa_name, AVG(mk.mark) AS mark_rating " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN marks AS mk ON f.film_id = mk.film_id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id " +
                //"HAVING AVG(mk.mark) > 5 " +
                "ORDER BY mark_rating DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), year, count);
    }

    @Override
    public List<Film> listTopFilmsByYear(int year) {
        String sql = "SELECT f.*, m.name AS mpa_name, AVG(mk.mark) AS mark_rating " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN marks AS mk ON f.film_id = mk.film_id " +
                "WHERE EXTRACT(year FROM f.release_date) = ? " +
                "GROUP BY f.film_id, m.name " +
                //"HAVING AVG(mk.mark) > 5 " +
                "ORDER BY mark_rating DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), year);
    }

    @Override
    public boolean deleteFilm(long id) {
        String sql = "delete from films where film_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public void clearDirectorsForFilm(long filmId) {
        String sql = "delete from film_director where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Long> findFilmIdsByTitleQuery(String query) {
        String sql = "select film_id from films " +
                "where lower(name) like :query";
        SqlParameterSource param =
                new MapSqlParameterSource("query", "%" + query.toLowerCase() + "%");
        return namedJdbcTemplate.queryForList(sql, param, Long.class);
    }

    @Override
    public List<Long> findFilmIdsByDirectorQuery(String query) {
        String sql = "select fd.film_id " +
                "from film_director as fd " +
                "join directors as d on fd.director_id = d.director_id " +
                "where lower(d.name) like :query";
        SqlParameterSource param =
                new MapSqlParameterSource("query", "%" + query.toLowerCase() + "%");
        return namedJdbcTemplate.queryForList(sql, param, Long.class);
    }

    @Override
    public List<Film> listTopFilmsByDirector(long directorId) {
        String sql = "SELECT f.*, m.name AS mpa_name, AVG(mk.mark) AS mark_rating " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "JOIN film_director AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN marks AS mk ON f.film_id = mk.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.film_id, m.name " +
                //"HAVING AVG(mk.mark) > 5 " +
                "ORDER BY mark_rating DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), directorId);
    }


    @Override
    public List<Long> findCommonFilmIds(Long userId, Long friendId) {
        String sql = "select film_id from marks " +
                "where user_id in (?, ?) " +
                "group by film_id having count(user_id) = 2 ";
        return jdbcTemplate.queryForList(sql, Long.class, userId, friendId);
    }

    @Override
    public Map<Long, HashMap<Long, Integer>> getUserIdsWithMarkedFilmIdsAndMarks() {
        String sql = "select user_id, film_id, mark from marks";
        final Map<Long, HashMap<Long, Integer>> userIdsFilmsIds = new HashMap<>();

        jdbcTemplate.query(sql,
                rs -> {
                    long userId = rs.getLong("user_id");
                    long filmId = rs.getLong("film_id");
                    int mark = rs.getInt("mark");
                    userIdsFilmsIds.computeIfAbsent(userId, k -> new HashMap<>())
                            .put(filmId, mark);
                });
        return userIdsFilmsIds;

    }

    @Override
    public void addDirectorToFilm(long filmId, long directorId) {
        String sql = "insert into film_director(film_id, director_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sql, filmId, directorId);
    }

    @Override
    public boolean addGenreToFilm(long filmId, int genreId) {
        String sql = "insert into film_genre(film_id, genre_id) " +
                "values (?, ?)";
        return jdbcTemplate.update(sql, filmId, genreId) > 0;
    }

    @Override
    public boolean deleteGenreFromFilm(long filmId, int genreId) {
        String sql = "delete from film_genre where (film_id = ? AND genre_id = ?)";
        return jdbcTemplate.update(sql, filmId, genreId) > 0;
    }

    @Override
    public boolean clearGenresFromFilm(long filmId) {
        String sql = "delete from film_genre where film_id = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public Map<Long, Integer> getMarksByFilm(long filmId) {
        String sql = "select user_id, mark from marks where film_id =?";
        Map<Long, Integer> userIdsMarks = new HashMap<>();
        jdbcTemplate.query(sql,
                rs -> {
                    long userId = rs.getLong("user_id");
                    int mark = rs.getInt("mark");
                    userIdsMarks.put(userId, mark);
                }, filmId);
        return userIdsMarks;
    }

    @Override
    public boolean addMark(long filmId, long userId, int mark) {
        String sql = "insert into marks (film_id, user_id, mark) " +
                "values (?, ?, ?)";
        return jdbcTemplate.update(sql, filmId, userId, mark) > 0;
    }

    @Override
    public boolean updateMark(long filmId, long userId, int mark) {
        String sql = "update marks set mark = ? where (film_id = ? AND user_id = ?)";
        return jdbcTemplate.update(sql, mark, filmId, userId) > 0;
    }

    @Override
    public boolean deleteMark(long filmId, long userId) {
        String sql = "delete from marks where (film_id = ? AND user_id = ?)";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_name");

        Mpa mpa = Mpa.builder()
                .id(mpaId)
                .name(mpaName)
                .build();

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .build();
    }
}