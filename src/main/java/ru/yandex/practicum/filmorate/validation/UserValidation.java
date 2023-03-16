package ru.yandex.practicum.filmorate.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidation {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static boolean isUserValid(User user, BindingResult bindingResult) throws JsonProcessingException {
        int validationErrors = bindingResult.getSuppressedFields().length;
        if (validationErrors != 0) {
            log.warn("Пользователь {} не прошел валидацию через аннотации. \n Количество ошибок: {}",
                    mapper.writeValueAsString(user), validationErrors);
            throw new ValidationException("Пользователь не прошел валидацию через аннотацию. " +
                    "Количество ошибок: " + validationErrors);
        }
        return isLoginValid(user) && isBirthdayValid(user);
    }

    private static boolean isBirthdayValid(User user) throws JsonProcessingException {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения {} не может быть в будущем", mapper.writeValueAsString(user.getBirthday()));
            throw new ValidationException("Дата рождения " + user.getBirthday() + "не может быть из будущего");
        }
        return true;
    }

    private static boolean isLoginValid(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин {} содержит пробелы", user.getLogin());
            throw new ValidationException("Логин " + user.getLogin() + " содержит пробелы");
        }
        return true;
    }
}
