package ru.kpfu.itis.semestrovka_2sem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.kpfu.itis.semestrovka_2sem.validation.ValidTutorSubjects;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@ValidTutorSubjects
public class TutorCreateDto {

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @Min(value = 0, message = "Опыт не может быть отрицательным")
    private Integer experience = 0;

    @Size(max = 1000, message = "Описание максимум 1000 символов")
    private String description;

    // Список выбранных предметов. Может быть пустым, если пользователь
    // вводит название нового предмета вручную. @NotNull гарантирует, что
    // набор передаётся в сервис в любом случае.
    @NotNull(message = "Набор предметов не может быть null")
    private Set<Long> subjectIds = new java.util.HashSet<>();

    /** Дополнительный предмет, если его нет в списке */
    @Size(max = 100, message = "Название предмета максимум 100 символов")
    private String newSubjectName;
}
