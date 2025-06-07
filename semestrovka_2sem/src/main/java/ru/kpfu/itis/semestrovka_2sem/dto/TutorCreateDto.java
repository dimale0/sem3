package ru.kpfu.itis.semestrovka_2sem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TutorCreateDto {

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @Min(value = 0, message = "Опыт не может быть отрицательным")
    private Integer experience = 0;

    @Size(max = 1000, message = "Описание максимум 1000 символов")
    private String description;

    @NotNull(message = "Набор предметов не может быть null")
    private Set<Long> subjectIds;
}
