// src/main/java/ru/kpfu/itis/semestrovka_2sem/controller/SubjectController.java
package ru.kpfu.itis.semestrovka_2sem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;
import ru.kpfu.itis.semestrovka_2sem.service.SubjectService;

import java.util.List;

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * Список всех предметов — доступно всем.
     */
    @GetMapping
    public String listAll(Model model) {
        List<Subject> subjects = subjectService.findAll();
        model.addAttribute("subjects", subjects);
        return "subject/list"; // src/main/resources/templates/subject/list.html
    }

    /**
     * Форма создания предмета — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "subject/create"; // src/main/resources/templates/subject/create.html
    }

    /**
     * Обработка POST-запроса создания — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/create")
    public String processCreate(
            @ModelAttribute("subject") Subject subject,
            Model model
    ) {
        try {
            subjectService.create(subject);
        } catch (Exception ex) {
            model.addAttribute("creationError", ex.getMessage());
            return "subject/create";
        }
        return "redirect:/subjects";
    }

    /**
     * Форма редактирования названия предмета — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Subject existing = subjectService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));
        model.addAttribute("subject", existing);
        return "subject/edit"; // src/main/resources/templates/subject/edit.html
    }

    /**
     * Обработка POST-запроса редактирования — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/edit/{id}")
    public String processEdit(
            @PathVariable Long id,
            @RequestParam("name") String newName,
            Model model
    ) {
        try {
            subjectService.updateName(id, newName);
        } catch (Exception ex) {
            model.addAttribute("editError", ex.getMessage());
            model.addAttribute("subject", subjectService.findById(id).orElse(new Subject()));
            return "subject/edit";
        }
        return "redirect:/subjects";
    }

    /**
     * Удаление предмета — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        subjectService.deleteById(id);
        return "redirect:/subjects";
    }
}
