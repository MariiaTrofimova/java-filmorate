package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseValidator implements
        ConstraintValidator<ReleaseConstraint, LocalDate> {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    @Override
    public void initialize(ReleaseConstraint birthday) {
    }

    @Override
    public boolean isValid(LocalDate date,
                           ConstraintValidatorContext cxt) {
        return (date != null) && (!date.isBefore(FIRST_FILM_DATE));
    }
}
