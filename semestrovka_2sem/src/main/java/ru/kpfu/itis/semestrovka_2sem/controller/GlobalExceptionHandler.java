package ru.kpfu.itis.semestrovka_2sem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.warn("Illegal argument: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error"; // templates/error.html
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex, Model model) {
        log.error("Unexpected error", ex);
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = "Внутренняя ошибка приложения";
        }
        model.addAttribute("errorMessage", message);
        return "error";
    }
}
