package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.Curriculum;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.repository.CurriculumRepository;
import com.example.academicfeedback.service.AiFeedbackService;
import com.example.academicfeedback.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class CurriculumController {

    private final CurriculumRepository curriculumRepository;
    private final SessionService sessionService;
    private final AiFeedbackService aiFeedbackService;

    public CurriculumController(CurriculumRepository curriculumRepository,
                                SessionService sessionService,
                                AiFeedbackService aiFeedbackService) {
        this.curriculumRepository = curriculumRepository;
        this.sessionService = sessionService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @GetMapping("/curriculums")
    public String curriculums(HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("curriculums", curriculumRepository.findAll());
        return "curriculums/index";
    }

    @GetMapping("/curriculums/new")
    public String newCurriculum(HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("curriculum", new Curriculum());
        model.addAttribute("formAction", "/curriculums");
        return "curriculums/form";
    }

    @PostMapping("/curriculums")
    public String createCurriculum(@ModelAttribute Curriculum curriculum, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        curriculum.setUpdatedAt(LocalDateTime.now());
        curriculumRepository.save(curriculum);
        return "redirect:/curriculums";
    }

    @GetMapping("/curriculums/{id}")
    public String curriculumDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("curriculum", curriculumRepository.findById(id).orElseThrow());
        return "curriculums/details";
    }

    @GetMapping("/curriculums/{id}/edit")
    public String editCurriculum(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("curriculum", curriculumRepository.findById(id).orElseThrow());
        model.addAttribute("formAction", "/curriculums/" + id);
        return "curriculums/form";
    }

    @PostMapping("/curriculums/{id}")
    public String updateCurriculum(@PathVariable Long id,
                                   @ModelAttribute Curriculum form,
                                   HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }

        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow();
        curriculum.setProgramName(form.getProgramName());
        curriculum.setCourseCode(form.getCourseCode());
        curriculum.setCourseTitle(form.getCourseTitle());
        curriculum.setCreditHours(form.getCreditHours());
        curriculum.setVersionName(form.getVersionName());
        curriculum.setEffectiveFrom(form.getEffectiveFrom());
        curriculum.setStatus(form.getStatus());
        curriculum.setCourseLearningOutcomes(form.getCourseLearningOutcomes());
        curriculum.setProgramLearningOutcomes(form.getProgramLearningOutcomes());
        curriculum.setIndustryAlignment(form.getIndustryAlignment());
        curriculum.setPeerBenchmarking(form.getPeerBenchmarking());
        curriculum.setEmergingTechnologyTrends(form.getEmergingTechnologyTrends());
        curriculum.setStakeholderFeedback(form.getStakeholderFeedback());
        curriculum.setRevisionProposal(form.getRevisionProposal());
        curriculum.setVersionHistory(form.getVersionHistory());
        curriculum.setUpdatedAt(LocalDateTime.now());
        curriculumRepository.save(curriculum);
        return "redirect:/curriculums/" + id;
    }

    @PostMapping("/curriculums/{id}/ai-gap")
    public String generateGapAnalysis(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }

        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow();
        String context = "Program: " + curriculum.getProgramName()
                + "\nCourse: " + curriculum.getCourseCode() + " - " + curriculum.getCourseTitle()
                + "\nCLOs: " + curriculum.getCourseLearningOutcomes()
                + "\nPLOs: " + curriculum.getProgramLearningOutcomes()
                + "\nIndustry alignment: " + curriculum.getIndustryAlignment()
                + "\nPeer benchmarking: " + curriculum.getPeerBenchmarking()
                + "\nEmerging technology trends: " + curriculum.getEmergingTechnologyTrends()
                + "\nStakeholder feedback: " + curriculum.getStakeholderFeedback()
                + "\nRevision proposal: " + curriculum.getRevisionProposal();
        curriculum.setAiGapAnalysis(aiFeedbackService.curriculumGapAnalysis(context));
        curriculum.setUpdatedAt(LocalDateTime.now());
        curriculumRepository.save(curriculum);
        return "redirect:/curriculums/" + id;
    }

    @PostMapping("/curriculums/{id}/delete")
    public String deleteCurriculum(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        curriculumRepository.deleteById(id);
        return "redirect:/curriculums";
    }
}
