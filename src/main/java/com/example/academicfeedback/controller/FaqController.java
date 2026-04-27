package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.FaqItem;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.CourseRepository;
import com.example.academicfeedback.repository.FaqItemRepository;
import com.example.academicfeedback.service.AiFeedbackService;
import com.example.academicfeedback.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FaqController {

    private final FaqItemRepository faqItemRepository;
    private final CourseRepository courseRepository;
    private final SessionService sessionService;
    private final AiFeedbackService aiFeedbackService;

    public FaqController(FaqItemRepository faqItemRepository,
                         CourseRepository courseRepository,
                         SessionService sessionService,
                         AiFeedbackService aiFeedbackService) {
        this.faqItemRepository = faqItemRepository;
        this.courseRepository = courseRepository;
        this.sessionService = sessionService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @GetMapping("/faqs")
    public String faqs(HttpSession session, Model model) {
        if (sessionService.currentUser(session).isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("faqs", faqItemRepository.findAll());
        return "faqs/index";
    }

    @GetMapping("/faqs/new")
    public String newFaq(HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("faqItem", new FaqItem());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("formAction", "/faqs");
        return "faqs/form";
    }

    @PostMapping("/faqs")
    public String createFaq(@ModelAttribute FaqItem faqItem,
                            @RequestParam(required = false) Long courseId,
                            @RequestParam(defaultValue = "false") boolean generateWithAi,
                            HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.FACULTY)) {
            return "redirect:/dashboard";
        }

        courseRepository.findById(courseId == null ? -1 : courseId).ifPresent(faqItem::setCourse);
        faqItem.setCreatedBy(user);
        if (generateWithAi || faqItem.getAnswer() == null || faqItem.getAnswer().isBlank()) {
            faqItem.setAnswer(aiFeedbackService.faqAnswer(faqContext(faqItem)));
            faqItem.setGeneratedByAi(true);
        }
        faqItemRepository.save(faqItem);
        return "redirect:/faqs";
    }

    @GetMapping("/faqs/{id}/edit")
    public String editFaq(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("faqItem", faqItemRepository.findById(id).orElseThrow());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("formAction", "/faqs/" + id);
        return "faqs/form";
    }

    @PostMapping("/faqs/{id}")
    public String updateFaq(@PathVariable Long id,
                            @ModelAttribute FaqItem form,
                            @RequestParam(required = false) Long courseId,
                            @RequestParam(defaultValue = "false") boolean generateWithAi,
                            HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }

        FaqItem faqItem = faqItemRepository.findById(id).orElseThrow();
        faqItem.setQuestion(form.getQuestion());
        faqItem.setCategory(form.getCategory());
        faqItem.setAnswer(form.getAnswer());
        faqItem.setCourse(null);
        courseRepository.findById(courseId == null ? -1 : courseId).ifPresent(faqItem::setCourse);
        if (generateWithAi) {
            faqItem.setAnswer(aiFeedbackService.faqAnswer(faqContext(faqItem)));
            faqItem.setGeneratedByAi(true);
        }
        faqItemRepository.save(faqItem);
        return "redirect:/faqs";
    }

    @PostMapping("/faqs/{id}/delete")
    public String deleteFaq(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        faqItemRepository.deleteById(id);
        return "redirect:/faqs";
    }

    private String faqContext(FaqItem faqItem) {
        String course = faqItem.getCourse() == null
                ? "General academic question"
                : faqItem.getCourse().getCode() + " - " + faqItem.getCourse().getTitle();
        return "Course: " + course
                + "\nCategory: " + faqItem.getCategory()
                + "\nQuestion: " + faqItem.getQuestion();
    }
}
