package ru.kpfu.itis.semestrovka_2sem.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.kpfu.itis.semestrovka_2sem.model.enums.UserRole;

@Getter
@Setter
public class UserRegisterDto {

    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 255, message = "Имя максимум 255 символов")
    private String name;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(max = 255, message = "Фамилия максимум 255 символов")
    private String surname;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    @Size(max = 255, message = "Email максимум 255 символов")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z]).+$",
            message = "Пароль должен содержать хотя бы одну цифру и одну заглавную букву"
    )
    private String rawPassword;

    @Size(max = 12, message = "Телефон максимум 12 символов")
    @Pattern(regexp = "(\\+7|8)[0-9]{10}", message = "Неверный формат телефона")
    private String phoneNumber;

    @NotNull(message = "Роль не может быть null")
    private UserRole role;
}
