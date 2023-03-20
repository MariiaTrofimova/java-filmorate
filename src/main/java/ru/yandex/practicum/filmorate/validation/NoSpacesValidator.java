package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoSpacesValidator implements
        ConstraintValidator<NoSpacesConstraint, String> {
    @Override
    public void initialize(NoSpacesConstraint noSpace) {
    }

    @Override
    public boolean isValid(String value,
                           ConstraintValidatorContext cxt) {
        return !value.contains(" ");
    }

}
