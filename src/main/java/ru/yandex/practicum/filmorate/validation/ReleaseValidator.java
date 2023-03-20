package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseValidator implements
        ConstraintValidator<ReleaseConstraint, LocalDate> {
    @Override
    public void initialize(ReleaseConstraint birthday) {
    }

    @Override
    public boolean isValid(LocalDate date,
                           ConstraintValidatorContext cxt) {
        return (date != null) && (!date.isBefore(LocalDate.of(1895, 12, 28)));
    }
}
