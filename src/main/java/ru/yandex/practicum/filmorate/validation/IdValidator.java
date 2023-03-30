package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import java.util.regex.Pattern;

@Slf4j
public class IdValidator {

    public static long parseId(String stringId) {
        if (stringId == null) {
            log.warn("id отсутствует");
            throw new ValidationException("id отсутствует");
        }
        if (Pattern.matches("^(-?[1-9]\\d*)|0$", stringId)){
            return Long.parseLong(stringId);
        } else {
            log.warn("Неверный формат id {}", stringId);
            throw new NumberFormatException("Неверный формат id");
        }
    }
}
