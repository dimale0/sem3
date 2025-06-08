package ru.kpfu.itis.semestrovka_2sem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Проверяет, что у заявки указан существующий предмет либо введено его название.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = SubjectIdOrNameRequiredValidator.class)
@Documented
public @interface SubjectIdOrNameRequired {
    String message() default "Нужно выбрать предмет или ввести название";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
