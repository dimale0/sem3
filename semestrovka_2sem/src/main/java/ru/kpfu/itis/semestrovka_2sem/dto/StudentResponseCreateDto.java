package ru.kpfu.itis.semestrovka_2sem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentResponseCreateDto {

    @NotNull(message = "StudentId обязателен")
    private Long studentId;

    @NotNull(message = "RequestId обязателен")
    private Long requestId;

    @NotBlank(message = "Сообщение не может быть пустым")
    @Size(max = 1000, message = "Сообщение максимум 1000 символов")
    private String message;
}
