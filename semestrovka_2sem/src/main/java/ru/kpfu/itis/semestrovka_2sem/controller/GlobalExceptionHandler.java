package ru.kpfu.itis.semestrovka_2sem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import jakarta.persistence.EntityNotFoundException;

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

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException ex, Model model) {
        log.warn("Entity not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDataIntegrity(DataIntegrityViolationException ex, Model model) {
        log.warn("Data integrity problem", ex);
        model.addAttribute("errorMessage", "Ошибка сохранения данных: " + ex.getMostSpecificCause().getMessage());
        return "error";
    }

    @ExceptionHandler({InvalidCsrfTokenException.class, CsrfException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleCsrf(CsrfException ex, Model model) {
        log.warn("CSRF error: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Сессия истекла, повторите запрос");
        return "error";
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
