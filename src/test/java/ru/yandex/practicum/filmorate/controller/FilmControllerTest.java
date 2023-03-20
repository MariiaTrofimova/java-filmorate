package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController controller;
    FilmService service;
    Film film;
    private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);
    private final int duration = 90;

    Film.FilmBuilder filmBuilder = Film.builder()
            .name("Film name")
            .description("Film description")
            .releaseDate(testReleaseDate)
            .duration(duration);

    @BeforeEach
    void setUp() {
        service = new FilmService();
        controller = new FilmController(service);
    }

    @Test
    void listFilms() throws JsonProcessingException {
        List<Film> films = controller.listFilms();
        assertNotNull(films, "Список фильмов не возвращается");
        assertEquals(films.size(), 0);

        film = filmBuilder.build();
        controller.addFilm(film);
        films = controller.listFilms();

        film.setId(1);
        assertNotNull(films, "Список фильмов не возвращается");
        assertEquals(films.size(), 1);
        assertEquals(film, films.get(0), "Фильмы не совпадают");

        Film film1 = filmBuilder
                .name("Film1 name")
                .description("Film1 description")
                .build();
        controller.addFilm(film1);
        film1.setId(2);

        films = controller.listFilms();
        assertNotNull(films, "Список фильмов не возвращается");
        assertEquals(films.size(), 2);
        assertEquals(film1, films.get(1), "Фильмы не совпадают");
    }

    @Test
    void addFilm() throws JsonProcessingException {
        film = filmBuilder
                .name("")
                .build();
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> controller.addFilm(film)
        );
        assertEquals("Отсутствует название", ex.getMessage());

        Film filmNoDescription = filmBuilder
                .name("Film1 name")
                .description("")
                .build();
        ex = assertThrows(
                ValidationException.class,
                () -> controller.addFilm(filmNoDescription)
        );
        assertEquals("Отсутствует описание", ex.getMessage());

        Film filmNoReleaseDate = filmBuilder
                .description("Film description")
                .releaseDate(null)
                .build();
        ex = assertThrows(
                ValidationException.class,
                () -> controller.addFilm(filmNoReleaseDate)
        );
        assertEquals("Отсутствует дата релиза", ex.getMessage());

        Film filmReleaseBefore1895 = filmBuilder
                .name("Сцена в саду Роундхэй")
                .description("Первый снятый фильм. Документальный, короткометражка")
                .releaseDate(LocalDate.of(1888, 10, 14))
                .duration(1)
                .build();
        ex = assertThrows(
                ValidationException.class,
                () -> controller.addFilm(filmReleaseBefore1895)
        );
        assertEquals("Дата релиза раньше 28 декабря 1895 года", ex.getMessage());

        Film filmRelease1895 = filmBuilder
                .name("Политый поливальщик")
                .description("Первая комедия в мире")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .build();
        Film filmAdded = controller.addFilm(filmRelease1895);
        assertEquals(filmAdded, filmRelease1895);

        Film filmDescription201 = filmBuilder
                .name("Всё везде и сразу")
                .description("Судьба мультивселенной в руках владелицы прачечной. " +
                        "Лучший фильм, режиссер и еще пять «Оскаров». " +
                        "Фантастика, комедия, боевик, приключения, драма. " +
                        "Режиссеры: Дэн Кван, Дэниэл Шайнерт. Актер: Мишель Йео.")
                .releaseDate(LocalDate.of(2022, 3, 11))
                .duration(139)
                .build();
        ex = assertThrows(
                ValidationException.class,
                () -> controller.addFilm(filmDescription201)
        );
        assertEquals("Длина описания больше 200 символов", ex.getMessage());

        Film filmDescription200 = filmBuilder
                .description("Судьба мультивселенной в руках владелицы прачечной. " +
                        "Лучший фильм, режиссер и еще пять «Оскаров». " +
                        "Фантастика, комедия, боевик, приключения, драма. " +
                        "Режиссеры: Дэн Кван, Дэниэл Шайнерт. Актер: Мишель Йео")
                .build();
        filmAdded = controller.addFilm(filmDescription200);
        assertEquals(filmAdded, filmDescription200);

        Film filmDurationNegative = filmBuilder
                .name("Назад в будущее")
                .description("Безумный ученый и 17-летний оболтус тестируют машину времени, наводя шороху в 1950-х. " +
                        "Классика кинофантастики")
                .releaseDate(LocalDate.of(1985, 7, 3))
                .duration(-1)
                .build();

        ex = assertThrows(
                ValidationException.class,
                () -> controller.addFilm(filmDurationNegative)
        );
        assertEquals("Продолжительность фильма отрицательная", ex.getMessage());

        Film filmDuration0 = filmBuilder
                .duration(0)
                .build();
        filmAdded = controller.addFilm(filmDuration0);
        assertEquals(filmAdded, filmDuration0);
    }

    @Test
    void updateFilm() throws JsonProcessingException {
        film = filmBuilder.build();
        Film filmToUpdate = controller.addFilm(film);
        filmToUpdate.setName("Film name updated");
        Film filmUpdated = controller.updateFilm(filmToUpdate);
        assertEquals(filmToUpdate, filmUpdated);

        Film filmId = filmBuilder.build();
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> controller.updateFilm(filmId)
        );
        assertEquals("Отсутствует id", ex.getMessage());

        filmId.setId(2);
        ex = assertThrows(
                NotFoundException.class,
                () -> controller.updateFilm(filmId)
        );
        assertEquals("Фильм с id 2 не найден", ex.getMessage());
    }

}