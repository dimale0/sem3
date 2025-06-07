package ru.kpfu.itis.semestrovka_2sem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;
import ru.kpfu.itis.semestrovka_2sem.service.SubjectService;
import ru.kpfu.itis.semestrovka_2sem.service.UserService;


import java.util.List;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final SubjectService subjectService;
    private final UserService userService;


    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/tutor")
    public String tutorHome() {
        return "home/tutor";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student")
    public String studentHome(Authentication authentication, Model model) {
        List<Subject> subjects = subjectService.findAll();
        model.addAttribute("subjects", subjects);
        Long userId = userService.findByEmail(authentication.getName())
                .map(u -> u.getId())
                .orElse(null);
        model.addAttribute("studentId", userId);

        return "home/student";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/student")
    public String chooseSubject(@RequestParam("subjectId") Long subjectId) {
        return "redirect:/requests?subjectId=" + subjectId;
    }
}
