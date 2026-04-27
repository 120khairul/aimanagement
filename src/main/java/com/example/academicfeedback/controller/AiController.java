package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.service.AiFeedbackService;
import com.example.academicfeedback.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AiController {

    private final AiFeedbackService aiFeedbackService;
    private final SessionService sessionService;

    public AiController(AiFeedbackService aiFeedbackService, SessionService sessionService) {
        this.aiFeedbackService = aiFeedbackService;
        this.sessionService = sessionService;
    }

    @GetMapping("/ai")
    public String aiTools(HttpSession session, Model model) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("taskType", "Student Feedback");
        model.addAttribute("prompt", "Draft feedback for a student submission.");
        model.addAttribute("context", "");
        return "ai/index";
    }

    @PostMapping("/ai/generate")
    public String generate(@RequestParam String taskType,
                           @RequestParam String prompt,
                           @RequestParam(required = false) String context,
                           HttpSession session,
                           Model model) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        String safeContext = context == null ? "" : context;
        String output = aiFeedbackService.customAcademicAssistant(taskType, prompt, safeContext);
        model.addAttribute("taskType", taskType);
        model.addAttribute("prompt", prompt);
        model.addAttribute("context", safeContext);
        model.addAttribute("output", output);
        return "ai/index";
    }
}
