package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Пользователь не прошел валидацию")
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
