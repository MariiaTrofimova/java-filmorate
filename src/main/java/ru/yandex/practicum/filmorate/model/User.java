package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private int id;
    @NotNull(message = "E-mail не может быть пустым")
    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "Введен некорректный e-mail")
    private String email;
    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;
}
