// src/main/java/ru/kpfu/itis/semestrovka_2sem/controller/StudentResponseController.java
package ru.kpfu.itis.semestrovka_2sem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.semestrovka_2sem.dto.StudentResponseCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.StudentResponse;
import ru.kpfu.itis.semestrovka_2sem.service.StudentResponseService;
import ru.kpfu.itis.semestrovka_2sem.service.TutorRequestService;
import ru.kpfu.itis.semestrovka_2sem.service.TutorService;
import ru.kpfu.itis.semestrovka_2sem.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/responses")
@RequiredArgsConstructor
public class StudentResponseController {

    private final StudentResponseService studentResponseService;
    private final UserService userService;
    private final TutorRequestService tutorRequestService;
    private final TutorService tutorService;

    /**
     * Список всех откликов конкретного студента — только тот студент.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/{studentId}")
    public String listByStudent(
            @PathVariable Long studentId,
            Authentication authentication,
            Model model
    ) {
        // Проверяем, что текущий юзер — тот же студент
        String currentEmail = authentication.getName();
        Long currentUserId = userService.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"))
                .getId();
        if (!currentUserId.equals(studentId)) {
            return "redirect:/"; // или другая страница
        }

        List<StudentResponse> responses = studentResponseService.findAllByStudentId(studentId);
        model.addAttribute("responses", responses);
        return "response/list-by-student"; // src/main/resources/templates/response/list-by-student.html
    }

    /**
     * Список всех откликов на конкретную заявку — только владелец заявки (репетитор).
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/request/{requestId}")
    public String listByRequest(
            @PathVariable Long requestId,
            Authentication authentication,
            Model model
    ) {
        // Проверяем, что текущий пользователь — репетитор, владеющий этой заявкой
        var optionalReq = tutorRequestService.findById(requestId);
        if (optionalReq.isEmpty()) {
            throw new IllegalArgumentException("Заявка не найдена");
        }
        var tutor = optionalReq.get().getTutor();
        String tutorEmail = tutor.getUser().getEmail();
        if (!tutorEmail.equals(authentication.getName())) {
            return "redirect:/"; // или другая страница
        }

        List<StudentResponse> responses = studentResponseService.findAllByRequestId(requestId);
        model.addAttribute("responses", responses);
        return "response/list-by-request"; // src/main/resources/templates/response/list-by-request.html
    }

    /**
     * Список всех откликов на заявки текущего репетитора.
     */
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/tutor")
    public String listByTutor(Authentication authentication, Model model) {
        var user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        var tutorOpt = tutorService.findByUser(user);
        if (tutorOpt.isEmpty()) {
            return "redirect:/tutors/create";
        }
        var tutor = tutorOpt.get();

        var requests = tutorRequestService.findAllByTutorId(tutor.getId());
        java.util.List<StudentResponse> all = new java.util.ArrayList<>();
        for (var req : requests) {
            all.addAll(studentResponseService.findAllByRequestId(req.getId()));
        }
        model.addAttribute("responses", all);
        return "response/list-by-tutor";
    }

    /**
     * Форма редактирования отклика — только владелец отклика (ROLE_STUDENT).
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model
    ) {
        StudentResponse existing = studentResponseService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Отклик не найден"));
        // Проверяем, что текущий пользователь — владелец отклика
        String currentEmail = authentication.getName();
        if (!existing.getStudent().getEmail().equals(currentEmail)) {
            return "redirect:/";
        }
        StudentResponseCreateDto dto = new StudentResponseCreateDto();
        dto.setStudentId(existing.getStudent().getId());
        dto.setRequestId(existing.getTutorRequest().getId());
        dto.setMessage(existing.getMessage());

        model.addAttribute("form", dto);
        model.addAttribute("responseId", id);
        return "response/edit"; // src/main/resources/templates/response/edit.html
    }

    /**
     * Обработка POST-запроса редактирования отклика — только владелец.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/edit/{id}")
    public String processEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") StudentResponseCreateDto form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        StudentResponse existing = studentResponseService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Отклик не найден"));
        if (!existing.getStudent().getEmail().equals(authentication.getName())) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("responseId", id);
            return "response/edit";
        }
        try {
            studentResponseService.update(id, form);
        } catch (Exception ex) {
            model.addAttribute("editError", ex.getMessage());
            model.addAttribute("responseId", id);
            return "response/edit";
        }
        return "redirect:/responses/student/" + form.getStudentId();
    }

    /**
     * Удаление отклика — только владелец.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            @RequestParam Long studentId,
            Authentication authentication
    ) {
        StudentResponse existing = studentResponseService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Отклик не найден"));
        if (!existing.getStudent().getEmail().equals(authentication.getName())) {
            return "redirect:/";
        }
        studentResponseService.deleteById(id);
        return "redirect:/responses/student/" + studentId;
    }
}
