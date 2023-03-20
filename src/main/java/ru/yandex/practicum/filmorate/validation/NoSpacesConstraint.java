package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpacesValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpacesConstraint {
    String message() default "Логин содержит пробелы";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
