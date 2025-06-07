// src/main/java/ru/kpfu/itis/semestrovka_2sem/controller/AuthController.java
package ru.kpfu.itis.semestrovka_2sem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для отображения страницы входа.
 * Spring Security будет перенаправлять сюда при необходимости.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("loginError", "Неверный email или пароль");
        }
        return "auth/login"; // templates/auth/login.html
    }
}
