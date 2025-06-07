// src/main/java/ru/kpfu/itis/semestrovka_2sem/controller/TutorRequestController.java
package ru.kpfu.itis.semestrovka_2sem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.semestrovka_2sem.dto.StudentResponseCreateDto;
import ru.kpfu.itis.semestrovka_2sem.dto.TutorRequestCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.TutorRequest;
import ru.kpfu.itis.semestrovka_2sem.model.Tutor;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;
import ru.kpfu.itis.semestrovka_2sem.model.User;
import ru.kpfu.itis.semestrovka_2sem.service.TutorRequestService;
import ru.kpfu.itis.semestrovka_2sem.service.TutorService;
import ru.kpfu.itis.semestrovka_2sem.service.UserService;
import ru.kpfu.itis.semestrovka_2sem.service.StudentResponseService;
import ru.kpfu.itis.semestrovka_2sem.service.SubjectService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class TutorRequestController {

    private final TutorRequestService tutorRequestService;
    private final TutorService tutorService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final StudentResponseService studentResponseService;

    /**
     * Список всех заявок — доступно всем.
     */
    @GetMapping
    public String listAll(@RequestParam(value = "subjectId", required = false) Long subjectId,
                          Model model) {
        List<TutorRequest> requests;
        if (subjectId != null) {
            Subject subject = subjectService.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));
            requests = tutorRequestService.findAllBySubjectName(subject.getName());
            model.addAttribute("selectedSubject", subject);
        } else {
            requests = tutorRequestService.findAll();
        }
        model.addAttribute("requests", requests);
        return "request/list"; // src/main/resources/templates/request/list.html
    }

    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/my")
    public String listMyRequests(Authentication authentication, Model model) {
        User currentUser = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        Tutor tutor = tutorService.findByUser(currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Профиль репетитора не найден"));

        List<TutorRequest> requests = tutorRequestService.findAllByTutorId(tutor.getId());
        model.addAttribute("requests", requests);
        return "request/list";
    }

    /**
     * Просмотр конкретной заявки (детали) — доступно всем.
     */
    @GetMapping("/view/{id}")
    public String viewOne(@PathVariable Long id, Model model) {
        TutorRequest req = tutorRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        model.addAttribute("request", req);
        return "request/view"; // src/main/resources/templates/request/view.html
    }

    /**
     * Форма создания новой заявки — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("form", new TutorRequestCreateDto());
        model.addAttribute("subjects", subjectService.findAll());
        return "request/create"; // src/main/resources/templates/request/create.html
    }

    /**
     * Обработка POST-запроса создания — только ROLE_TUTOR.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/create")
    public String processCreate(
            @ModelAttribute("form") TutorRequestCreateDto form,
            Authentication authentication,
            Model model
    ) {
        try {
            User currentUser = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            Tutor tutor = tutorService.findByUser(currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Профиль репетитора не найден"));
            form.setTutorId(tutor.getId());
            tutorRequestService.create(form);
        } catch (Exception ex) {
            model.addAttribute("creationError", ex.getMessage());
            model.addAttribute("subjects", subjectService.findAll());
            return "request/create";
        }
        return "redirect:/requests";
    }

    /**
     * Форма редактирования заявки — только владелец.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Authentication authentication, Model model) {
        TutorRequest existing = tutorRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        // Проверяем, что текущий пользователь — владелец заявки
        if (!existing.getTutor().getUser().getEmail().equals(authentication.getName())) {
            return "redirect:/requests";
        }
        TutorRequestCreateDto dto = new TutorRequestCreateDto();
        dto.setTutorId(existing.getTutor().getId());
        dto.setSubjectId(existing.getSubject().getId());
        dto.setPrice(existing.getPrice());
        dto.setDuration(existing.getDuration());
        dto.setDescription(existing.getDescription());

        model.addAttribute("form", dto);
        model.addAttribute("requestId", id);
        model.addAttribute("subjects", subjectService.findAll());
        return "request/edit"; // src/main/resources/templates/request/edit.html
    }

    /**
     * Обработка POST-запроса редактирования заявки — только владелец.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/edit/{id}")
    public String processEdit(
            @PathVariable Long id,
            @ModelAttribute("form") TutorRequestCreateDto form,
            Authentication authentication,
            Model model
    ) {
        TutorRequest existing = tutorRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (!existing.getTutor().getUser().getEmail().equals(authentication.getName())) {
            return "redirect:/requests";
        }
        try {
            form.setTutorId(existing.getTutor().getId());
            tutorRequestService.update(id, form);
        } catch (Exception ex) {
            model.addAttribute("editError", ex.getMessage());
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("requestId", id);
            return "request/edit";
        }
        return "redirect:/requests";
    }

    /**
     * Удаление заявки — только владелец.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Authentication authentication) {
        TutorRequest existing = tutorRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (!existing.getTutor().getUser().getEmail().equals(authentication.getName())) {
            return "redirect:/requests";
        }
        tutorRequestService.deleteById(id);
        return "redirect:/requests";
    }

    /**
     * AJAX: студент откликается на заявку — только ROLE_STUDENT.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{id}/respond")
    @ResponseBody
    public ResponseEntity<?> respondToRequest(
            @PathVariable("id") Long requestId,
            @RequestBody StudentResponseCreateDto responseDto,
            Authentication authentication
    ) {
        try {
            User currentStudent = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            responseDto.setStudentId(currentStudent.getId());
            responseDto.setRequestId(requestId);
            studentResponseService.create(responseDto);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body("{\"status\":\"error\",\"message\":\"" + ex.getMessage() + "\"}");
        }
    }
}
