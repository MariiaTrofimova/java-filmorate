package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    User user;
    String url = "/users";

    User.UserBuilder userBuilder;

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @BeforeEach
    void setupBuilder() {
        userBuilder = User.builder()
                .email("e@mail.ru")
                .login("Login")
                .birthday(LocalDate.of(1985, 9, 7));
    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldReturnEmptyListUsers() throws Exception {
        when(service.listUsers()).thenReturn(Collections.EMPTY_LIST);
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnSingleListUsers() throws Exception {
        when(service.listUsers()).thenReturn(List.of(
                userBuilder.id(1).login("Login1").email("e1@mail.ru").name("name1").build()));
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void shouldReturnListOfTwoUsers() throws Exception {
        when(service.listUsers()).thenReturn(List.of(
                userBuilder.id(1).login("Login1").email("e1@mail.ru").name("name1").build(),
                userBuilder.id(2).login("Login2").email("e2@mail.ru").name("name2").build()
        ));

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].login", containsInAnyOrder("Login1", "Login2")));
    }

    @Test
    void addRegularUser() throws Exception {
        user = userBuilder.build();
        User userAdded = userBuilder.id(1).name("Login").build();
        String json = mapper.writeValueAsString(user);
        String jsonAdded = mapper.writeValueAsString(userAdded);

        when(service.addUser(user)).thenReturn(userAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void addNewbornUser() throws Exception {
        user = userBuilder.birthday(LocalDate.now()).build();
        User userAdded = userBuilder.id(1).name("Login").birthday(LocalDate.now()).build();
        String json = mapper.writeValueAsString(user);
        String jsonAdded = mapper.writeValueAsString(userAdded);

        when(service.addUser(user)).thenReturn(userAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void addUserFailLogin() throws Exception {
        user = userBuilder.login("lo gin").build();
        String json = mapper.writeValueAsString(user);

        when(service.addUser(user)).thenReturn(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Логин содержит пробелы"));

        user = userBuilder.login("").build();
        json = mapper.writeValueAsString(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Логин не может быть пустым"));
    }

    @Test
    void addUserFailEmail() throws Exception {
        user = userBuilder.email("").build();
        String json = mapper.writeValueAsString(user);

        when(service.addUser(user)).thenReturn(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("E-mail не может быть пустым"));

        user = userBuilder.email("email@").build();
        json = mapper.writeValueAsString(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Введен некорректный e-mail"));
    }

    @Test
    void addUserFailBirthday() throws Exception {
        user = userBuilder.birthday(null).build();
        String json = mapper.writeValueAsString(user);
        System.out.println(json);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Дата рождения не может быть пустой"));

        user = userBuilder.birthday(LocalDate.now().plusDays(1)).build();
        json = mapper.writeValueAsString(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getMessage().equals("Дата рождения не может быть из будущего"));
    }

    @Test
    void updateUserExistingId() throws Exception {
        user = userBuilder.id(1).name("Name").build();
        String json = mapper.writeValueAsString(user);

        when(service.updateUser(user)).thenReturn(user);
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void updateUserNotExistingId() throws Exception {
        user = userBuilder.id(0).name("Name").build();
        String json = mapper.writeValueAsString(user);

        when(service.updateUser(user)).thenThrow(NotFoundException.class);
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound());

        user = userBuilder.id(1).build();
        json = mapper.writeValueAsString(user);

        when(service.updateUser(user)).thenThrow(NotFoundException.class);
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}