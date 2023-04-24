package ru.yandex.practicum.filmorate.storage.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public List<Film> listFilms() {
        String sql = "select * from films;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Film findFilmById(long id) {
        String sql = "select * from films where film_id = ?";
        Optional<Film> filmOptional = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs)).stream().findAny();
        if (filmOptional.isPresent()) {
            return filmOptional.get();
        }
        log.warn("Фильм с id {} не найден", id);
        throw new NotFoundException(String.format("Фильм с id %d не найден", id));
    }

    @Override
    @SneakyThrows
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        //film.getGenres().forEach(genreId -> filmGenreDao.addGenreToFilm(film.getId(), genreId));
        log.debug("Фильм {} сохранен", mapper.writeValueAsString(film));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        //film.getGenres().forEach(genreId -> filmGenreDao.addGenreToFilm(film.getId(), genreId));
        return film;
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        //Mpa mpa = mpaDao.findMpaById(rs.getInt("mpa_id"));
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
      /*  filmGenreDao.getGenresByFilm(id)
                .forEach(film::addGenre);*/
    }
}
