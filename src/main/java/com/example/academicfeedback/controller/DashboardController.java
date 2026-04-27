package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.service.DashboardService;
import com.example.academicfeedback.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final SessionService sessionService;

    public DashboardController(DashboardService dashboardService, SessionService sessionService) {
        this.dashboardService = dashboardService;
        this.sessionService = sessionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("stats", dashboardService.stats());
        model.addAttribute("submissions", dashboardService.submissionsFor(user));
        switch (user.getRole()) {
            case ADMIN -> {
                model.addAttribute("dashboardTitle", "Admin Control Center");
                model.addAttribute("dashboardMessage", "Manage users, courses, curriculum records, FAQs, capstone projects, and the full academic workflow.");
                model.addAttribute("activityTitle", "All Academic Activity");
            }
            case FACULTY -> {
                model.addAttribute("dashboardTitle", "Faculty Teaching Dashboard");
                model.addAttribute("dashboardMessage", "Create course materials, evaluate submissions, monitor engagement, draft feedback, and supervise capstone work.");
                model.addAttribute("activityTitle", "Your Course Submissions");
            }
            case STUDENT -> {
                model.addAttribute("dashboardTitle", "Student Learning Dashboard");
                model.addAttribute("dashboardMessage", "View courses, submit assignments, read feedback, request rechecks, and track your capstone progress.");
                model.addAttribute("activityTitle", "Your Submissions and Feedback");
            }
        }
        return "dashboard/index";
    }
}
