// src/main/java/ru/kpfu/itis/semestrovka_2sem/controller/TutorController.java
package ru.kpfu.itis.semestrovka_2sem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.semestrovka_2sem.dto.TutorCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.Tutor;
import ru.kpfu.itis.semestrovka_2sem.model.User;
import ru.kpfu.itis.semestrovka_2sem.service.SubjectService;
import ru.kpfu.itis.semestrovka_2sem.service.TutorService;
import ru.kpfu.itis.semestrovka_2sem.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tutors")
@RequiredArgsConstructor
public class TutorController {

    private final TutorService tutorService;
    private final SubjectService subjectService;
    private final UserService userService;

    /**
     * Список всех репетиторов — доступно всем.
     */
    @GetMapping
    public String listAll(Model model) {
        List<Tutor> tutors = tutorService.findAll();
        model.addAttribute("tutors", tutors);
        return "tutor/list"; // src/main/resources/templates/tutor/list.html
    }

    /**
     * Просмотр профиля репетитора по ID — доступно всем.
     */
    @GetMapping("/{id}")
    public String viewProfile(@PathVariable Long id, Model model) {
        Tutor tutor = tutorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Репетитор не найден"));
        model.addAttribute("tutor", tutor);
        return "tutor/profile"; // src/main/resources/templates/tutor/profile.html
    }

    /**
     * Форма создания профиля репетитора — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        Long userId = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"))
                .getId();
        TutorCreateDto dto = new TutorCreateDto();
        dto.setUserId(userId);
        model.addAttribute("form", dto);
        model.addAttribute("subjects", subjectService.findAll());
        return "tutor/create"; // src/main/resources/templates/tutor/create.html
    }

    /**
     * Обработка POST-запроса создания профиля репетитора — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/create")
    public String processCreate(
            @Valid @ModelAttribute("form") TutorCreateDto form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subjects", subjectService.findAll());
            return "tutor/create";
        }
        try {
            Long userId = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"))
                    .getId();
            form.setUserId(userId);
            tutorService.create(form);
        } catch (Exception ex) {
            model.addAttribute("creationError", ex.getMessage());
            model.addAttribute("subjects", subjectService.findAll());
            return "tutor/create";
        }
        return "redirect:/tutors";
    }

    /**
     * Форма редактирования профиля репетитора — только владелец.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Authentication authentication, Model model) {
        Tutor existing = tutorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Репетитор не найден"));

        // Проверяем, что текущий пользователь — владелец профиля
        String currentEmail = authentication.getName();
        if (!existing.getUser().getEmail().equals(currentEmail)) {
            return "redirect:/tutors";
        }

        TutorCreateDto dto = new TutorCreateDto();
        dto.setUserId(existing.getUser().getId());
        dto.setExperience(existing.getExperience());
        dto.setDescription(existing.getDescription());
        Set<Long> subjectIds = existing.getSubjects()
                .stream()
                .map(subject -> subject.getId())
                .collect(Collectors.toSet());
        dto.setSubjectIds(subjectIds);

        model.addAttribute("form", dto);
        model.addAttribute("tutorId", id);
        model.addAttribute("subjects", subjectService.findAll());
        return "tutor/edit"; // src/main/resources/templates/tutor/edit.html
    }

    /**
     * Обработка POST-запроса редактирования профиля — только владелец.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/edit/{id}")
    public String processEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") TutorCreateDto form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        Tutor existing = tutorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Репетитор не найден"));
        String currentEmail = authentication.getName();
        if (!existing.getUser().getEmail().equals(currentEmail)) {
            return "redirect:/tutors";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("tutorId", id);
            return "tutor/edit";
        }
        try {
            form.setUserId(existing.getUser().getId());
            tutorService.update(id, form);
        } catch (Exception ex) {
            model.addAttribute("editError", ex.getMessage());
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("tutorId", id);
            return "tutor/edit";
        }
        return "redirect:/tutors";
    }

    /**
     * Удаление профиля репетитора — только владелец.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Authentication authentication) {
        Tutor existing = tutorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Репетитор не найден"));
        if (!existing.getUser().getEmail().equals(authentication.getName())) {
            return "redirect:/tutors";
        }
        tutorService.deleteById(id);
        return "redirect:/tutors";
    }
}
