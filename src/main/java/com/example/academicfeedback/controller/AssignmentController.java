package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.Assignment;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.AssignmentRepository;
import com.example.academicfeedback.repository.CourseRepository;
import com.example.academicfeedback.repository.SubmissionRepository;
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
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;
    private final SessionService sessionService;

    public AssignmentController(AssignmentRepository assignmentRepository,
                                CourseRepository courseRepository,
                                SubmissionRepository submissionRepository,
                                SessionService sessionService) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.submissionRepository = submissionRepository;
        this.sessionService = sessionService;
    }

    @GetMapping("/assignments/new")
    public String newAssignment(@RequestParam(required = false) Long courseId,
                                HttpSession session,
                                Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        Assignment assignment = new Assignment();
        courseRepository.findById(courseId == null ? -1 : courseId).ifPresent(assignment::setCourse);
        model.addAttribute("assignment", assignment);
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("formAction", "/assignments");
        return "assignments/form";
    }

    @PostMapping("/assignments")
    public String createAssignment(@ModelAttribute Assignment assignment,
                                   @RequestParam Long courseId,
                                   HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        assignment.setCourse(courseRepository.findById(courseId).orElseThrow());
        assignmentRepository.save(assignment);
        return "redirect:/courses/" + courseId;
    }

    @GetMapping("/assignments/{id}/edit")
    public String editAssignment(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("assignment", assignmentRepository.findById(id).orElseThrow());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("formAction", "/assignments/" + id);
        return "assignments/form";
    }

    @PostMapping("/assignments/{id}")
    public String updateAssignment(@PathVariable Long id,
                                   @ModelAttribute Assignment form,
                                   @RequestParam Long courseId,
                                   HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        Assignment assignment = assignmentRepository.findById(id).orElseThrow();
        assignment.setTitle(form.getTitle());
        assignment.setInstructions(form.getInstructions());
        assignment.setDueDate(form.getDueDate());
        assignment.setMaxMarks(form.getMaxMarks());
        assignment.setCourse(courseRepository.findById(courseId).orElseThrow());
        assignmentRepository.save(assignment);
        return "redirect:/assignments/" + id;
    }

    @GetMapping("/assignments/{id}")
    public String assignmentDetails(@PathVariable Long id, HttpSession session, Model model) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Assignment assignment = assignmentRepository.findById(id).orElseThrow();
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", submissionRepository.findByAssignmentId(id));
        if (user.getRole() == Role.STUDENT) {
            model.addAttribute("mySubmission",
                    submissionRepository.findByAssignmentIdAndStudentId(id, user.getId()).orElse(null));
        }
        return "assignments/details";
    }

    @PostMapping("/assignments/{id}/delete")
    public String deleteAssignment(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        Assignment assignment = assignmentRepository.findById(id).orElseThrow();
        Long courseId = assignment.getCourse().getId();
        assignmentRepository.deleteById(id);
        return "redirect:/courses/" + courseId;
    }
}
