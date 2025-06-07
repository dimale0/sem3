// src/main/java/ru/kpfu/itis/semestrovka_2sem/controller/UserController.java
package ru.kpfu.itis.semestrovka_2sem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.semestrovka_2sem.dto.UserRegisterDto;
import ru.kpfu.itis.semestrovka_2sem.model.User;
import ru.kpfu.itis.semestrovka_2sem.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Список всех пользователей — доступно только роли ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listAll(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user/list"; // src/main/resources/templates/user/list.html
    }

    /**
     * Форма регистрации нового пользователя — доступна всем.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("form", new UserRegisterDto());
        return "user/register"; // src/main/resources/templates/user/register.html
    }

    /**
     * Обработка POST-запроса регистрации — доступна всем.
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("form") UserRegisterDto form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }
        try {
            userService.register(form);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "user/register";
        }
        return "redirect:/login";
    }

    /**
     * Просмотр профиля пользователя по ID — доступно аутентифицированным.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String viewProfile(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        model.addAttribute("user", user);
        return "user/profile"; // src/main/resources/templates/user/profile.html
    }
}
