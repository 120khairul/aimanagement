package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.CapstoneStatus;
import com.example.academicfeedback.model.CurriculumStatus;
import com.example.academicfeedback.model.MaterialType;
import com.example.academicfeedback.model.MilestoneStatus;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.service.AiFeedbackService;
import com.example.academicfeedback.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final SessionService sessionService;
    private final AiFeedbackService aiFeedbackService;

    public GlobalModelAdvice(SessionService sessionService, AiFeedbackService aiFeedbackService) {
        this.sessionService = sessionService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @ModelAttribute("currentUser")
    public UserAccount currentUser(HttpSession session) {
        return sessionService.currentUser(session).orElse(null);
    }

    @ModelAttribute("roles")
    public Role[] roles() {
        return Role.values();
    }

    @ModelAttribute("materialTypes")
    public MaterialType[] materialTypes() {
        return MaterialType.values();
    }

    @ModelAttribute("curriculumStatuses")
    public CurriculumStatus[] curriculumStatuses() {
        return CurriculumStatus.values();
    }

    @ModelAttribute("capstoneStatuses")
    public CapstoneStatus[] capstoneStatuses() {
        return CapstoneStatus.values();
    }

    @ModelAttribute("milestoneStatuses")
    public MilestoneStatus[] milestoneStatuses() {
        return MilestoneStatus.values();
    }

    @ModelAttribute("openAiEnabled")
    public boolean openAiEnabled() {
        return aiFeedbackService.isOpenAiConfigured();
    }

    @ModelAttribute("openAiModel")
    public String openAiModel() {
        return aiFeedbackService.getModel();
    }
}
