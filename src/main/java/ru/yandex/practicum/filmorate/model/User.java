package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.BirthdayConstraint;
import ru.yandex.practicum.filmorate.validation.NoSpacesConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private int id;
    @NotNull(message = "E-mail не может быть пустым")
    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "Введен некорректный e-mail")
    private String email;
    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может быть пустым")
    @NoSpacesConstraint
    private String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть пустой")
    @BirthdayConstraint
    private LocalDate birthday;
}
