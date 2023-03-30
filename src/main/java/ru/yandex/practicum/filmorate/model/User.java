package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private long id;
    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "Введен некорректный e-mail")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть из будущего")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(long id) {
        friends.add(id);
    }

    public boolean deleteFriend(long id) {
        if (!friends.contains(id)) {
            return false;
        }
        friends.remove(id);
        return true;
    }
}
