package ru.kpfu.itis.semestrovka_2sem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.kpfu.itis.semestrovka_2sem.dto.TutorCreateDto;

public class ValidTutorSubjectsValidator implements ConstraintValidator<ValidTutorSubjects, TutorCreateDto> {
    @Override
    public boolean isValid(TutorCreateDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // другие проверки поймают null
        }
        boolean hasIds = value.getSubjectIds() != null && !value.getSubjectIds().isEmpty();
        boolean hasNew = value.getNewSubjectName() != null && !value.getNewSubjectName().isBlank();
        if (hasIds || hasNew) {
            return true;
        }
        // привязываем сообщение к полю subjectIds, чтобы отображалось рядом с чекбоксами
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("subjectIds")
                .addConstraintViolation();
        return false;
    }
}
