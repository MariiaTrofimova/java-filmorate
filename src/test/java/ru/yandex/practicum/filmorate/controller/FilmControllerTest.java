package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService service;

    Film film;
    String url = "/films";
    private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);
    private final int duration = 90;

    Film.FilmBuilder filmBuilder;

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @BeforeEach
    void setupBuilder() {
        filmBuilder = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(testReleaseDate)
                .duration(duration);
    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldReturnEmptyListFilms() throws Exception {
        when(service.listFilms()).thenReturn(Collections.EMPTY_LIST);
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnSingleListUsers() throws Exception {
        when(service.listFilms()).thenReturn(List.of(
                filmBuilder.id(1).build()));
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void shouldReturnListOfTwoUsers() throws Exception {
        when(service.listFilms()).thenReturn(List.of(
                filmBuilder.id(1).name("Film name1").build(),
                filmBuilder.id(2).name("Film name2").build()
        ));

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Film name1", "Film name2")));
    }

    @Test
    void addRegularFilm() throws Exception {
        film = filmBuilder.build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = mapper.writeValueAsString(film);
        String jsonAdded = mapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void addFirstFilm() throws Exception {
        film = filmBuilder
                .name("Политый поливальщик")
                .description("Первая комедия в мире")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = mapper.writeValueAsString(film);
        String jsonAdded = mapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void addFilmFailRelease() throws Exception {
        film = filmBuilder
                .name("Сцена в саду Роундхэй")
                .description("Первый снятый фильм. Документальный, короткометражка")
                .releaseDate(LocalDate.of(1888, 10, 14))
                .duration(1)
                .build();
        String json = mapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Дата релиза раньше 28 декабря 1895 года"));

        film = filmBuilder.releaseDate(null).build();
        json = mapper.writeValueAsString(film);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Отсутствует дата релиза"));
    }

    @Test
    void addFilmDescription200() throws Exception {
        film = filmBuilder
                .name("Всё везде и сразу")
                .description("Судьба мультивселенной в руках владелицы прачечной. " +
                        "Лучший фильм, режиссер и еще пять «Оскаров». " +
                        "Фантастика, комедия, боевик, приключения, драма. " +
                        "Режиссеры: Дэн Кван, Дэниэл Шайнерт. Актер: Мишель Йео")
                .releaseDate(LocalDate.of(2022, 3, 11))
                .duration(139)
                .build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = mapper.writeValueAsString(film);
        String jsonAdded = mapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void addFilmFailDescription() throws Exception {
        film = filmBuilder
                .name("Всё везде и сразу")
                .description("Судьба мультивселенной в руках владелицы прачечной. " +
                        "Лучший фильм, режиссер и еще пять «Оскаров». " +
                        "Фантастика, комедия, боевик, приключения, драма. " +
                        "Режиссеры: Дэн Кван, Дэниэл Шайнерт. Актер: Мишель Йео.")
                .releaseDate(LocalDate.of(2022, 3, 11))
                .duration(139)
                .build();
        String json = mapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Длина описания больше 200 символов"));

        film = filmBuilder.description("").build();
        json = mapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Отсутствует описание"));
    }

    @Test
    public void addFilmFailName() throws Exception {
        film = filmBuilder
                .name("")
                .build();
        String json = mapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Отсутствует название"));
    }

    @Test
    public void addFilmFailDuration() throws Exception {
        film = filmBuilder
                .name("Назад в будущее")
                .description("Безумный ученый и 17-летний оболтус тестируют машину времени, наводя шороху в 1950-х. " +
                        "Классика кинофантастики")
                .releaseDate(LocalDate.of(1985, 7, 3))
                .duration(-1)
                .build();
        String json = mapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Продолжительность фильма отрицательная"));
    }

    @Test
    public void addFilmDuration0() throws Exception {
        film = filmBuilder
                .duration(0)
                .build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = mapper.writeValueAsString(film);
        String jsonAdded = mapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));

        Film filmWithoutDuration = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(testReleaseDate)
                .build();
        filmAdded = filmBuilder.id(1).duration(0).build();
        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void updateFilmExistingId() throws Exception {
        film = filmBuilder.id(1).build();
        String json = mapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenReturn(film);
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void updateFilmNotExistingId() throws Exception {
        film = filmBuilder.build();
        String json = mapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenThrow(new NotFoundException("Отсутствует id"));
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Отсутствует id"));

        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenThrow(new NotFoundException("Фильм с id 1 не найден"));
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Фильм с id 1 не найден"));
    }

}