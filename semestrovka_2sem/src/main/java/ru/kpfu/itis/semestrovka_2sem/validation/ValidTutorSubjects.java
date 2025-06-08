package ru.kpfu.itis.semestrovka_2sem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Проверяет, что у репетитора выбран хотя бы один предмет
 * либо введено название нового предмета.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTutorSubjectsValidator.class)
@Documented
public @interface ValidTutorSubjects {
    String message() default "Нужно выбрать хотя бы один предмет";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
