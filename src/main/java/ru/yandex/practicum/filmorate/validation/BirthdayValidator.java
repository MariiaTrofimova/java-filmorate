package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayValidator implements
        ConstraintValidator<BirthdayConstraint, LocalDate> {
    @Override
    public void initialize(BirthdayConstraint birthday) {
    }

    @Override
    public boolean isValid(LocalDate date,
                           ConstraintValidatorContext cxt) {
        return (date != null) && (!date.isAfter(LocalDate.now()));
    }
}
