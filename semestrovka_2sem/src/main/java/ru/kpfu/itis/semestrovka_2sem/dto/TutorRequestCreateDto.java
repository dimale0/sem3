package ru.kpfu.itis.semestrovka_2sem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorRequestCreateDto {

    @NotNull(message = "Tutor ID обязателен")
    private Long tutorId;

    @NotNull(message = "Subject ID обязателен")
    private Long subjectId;

    @NotNull(message = "Цена обязательна")
    @Min(value = 1, message = "Цена должна быть больше 0")
    private Integer price;

    @NotNull(message = "Длительность обязательна")
    private Integer duration; // будем проверять 45, 60 или 90 в сервисе

    @Size(max = 1000, message = "Описание максимум 1000 символов")
    private String description;
}
