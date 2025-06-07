package ru.kpfu.itis.semestrovka_2sem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;
import ru.kpfu.itis.semestrovka_2sem.service.SubjectService;

import java.util.List;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final SubjectService subjectService;

    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/tutor")
    public String tutorHome() {
        return "home/tutor";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student")
    public String studentHome(Model model) {
        List<Subject> subjects = subjectService.findAll();
        model.addAttribute("subjects", subjects);
        return "home/student";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/student")
    public String chooseSubject(@RequestParam("subjectId") Long subjectId) {
        return "redirect:/requests?subjectId=" + subjectId;
    }
}
