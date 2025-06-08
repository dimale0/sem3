package ru.kpfu.itis.semestrovka_2sem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.kpfu.itis.semestrovka_2sem.dto.TutorRequestCreateDto;

public class SubjectIdOrNameRequiredValidator implements ConstraintValidator<SubjectIdOrNameRequired, TutorRequestCreateDto> {
    @Override
    public boolean isValid(TutorRequestCreateDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // другие аннотации обработают null
        }
        boolean hasId = value.getSubjectId() != null;
        boolean hasName = value.getSubjectName() != null && !value.getSubjectName().isBlank();
        if (hasId || hasName) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("subjectId")
                .addConstraintViolation();
        return false;
    }
}
