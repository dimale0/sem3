package ru.kpfu.itis.semestrovka_2sem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorRequestCreateDto {

    // ID репетитора будет подставлен контроллером, поэтому не валидируем здесь
    private Long tutorId;

    // Идентификатор предмета, если выбираем существующий
    private Long subjectId;

    // Название предмета, если хотим ввести вручную
    @NotBlank(message = "Название предмета обязательно")
    private String subjectName;

    @NotNull(message = "Цена обязательна")
    @Min(value = 1, message = "Цена должна быть больше 0")
    private Integer price;

    @NotNull(message = "Длительность обязательна")
    private Integer duration; // будем проверять 45, 60 или 90 в сервисе

    @Size(max = 1000, message = "Описание максимум 1000 символов")
    private String description;
}
