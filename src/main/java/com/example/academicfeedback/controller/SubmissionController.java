package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.Assignment;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.Submission;
import com.example.academicfeedback.model.SubmissionStatus;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.AssignmentRepository;
import com.example.academicfeedback.repository.SubmissionRepository;
import com.example.academicfeedback.service.AiFeedbackService;
import com.example.academicfeedback.service.GradingService;
import com.example.academicfeedback.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class SubmissionController {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final SessionService sessionService;
    private final GradingService gradingService;
    private final AiFeedbackService aiFeedbackService;

    public SubmissionController(AssignmentRepository assignmentRepository,
                                SubmissionRepository submissionRepository,
                                SessionService sessionService,
                                GradingService gradingService,
                                AiFeedbackService aiFeedbackService) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.sessionService = sessionService;
        this.gradingService = gradingService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public String submit(@PathVariable Long assignmentId,
                         @RequestParam String content,
                         HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null || user.getRole() != Role.STUDENT) {
            return "redirect:/dashboard";
        }

        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
        Submission submission = submissionRepository
                .findByAssignmentIdAndStudentId(assignmentId, user.getId())
                .orElse(new Submission(assignment, user, content));
        submission.setContent(content);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submissionRepository.save(submission);
        return "redirect:/assignments/" + assignmentId;
    }

    @PostMapping("/submissions/{id}/grade")
    public String grade(@PathVariable Long id,
                        @RequestParam Double marks,
                        @RequestParam(required = false) String feedback,
                        HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }

        Submission submission = submissionRepository.findById(id).orElseThrow();
        submission.setMarks(marks);
        submission.setGrade(gradingService.calculateGrade(marks, submission.getAssignment().getMaxMarks()));
        submission.setStatus(SubmissionStatus.GRADED);
        if (feedback == null || feedback.isBlank()) {
            submission.setFeedback(aiFeedbackService.draftFeedback(submission));
        } else {
            submission.setFeedback(feedback);
        }
        submissionRepository.save(submission);
        return "redirect:/assignments/" + submission.getAssignment().getId();
    }

    @PostMapping("/submissions/{id}/recheck")
    public String requestRecheck(@PathVariable Long id,
                                 @RequestParam String recheckReason,
                                 HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null || user.getRole() != Role.STUDENT) {
            return "redirect:/dashboard";
        }

        Submission submission = submissionRepository.findById(id).orElseThrow();
        if (!submission.getStudent().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        submission.setRecheckReason(recheckReason);
        submission.setStatus(SubmissionStatus.RECHECK_REQUESTED);
        submissionRepository.save(submission);
        return "redirect:/assignments/" + submission.getAssignment().getId();
    }

    @PostMapping("/submissions/{id}/resubmission")
    public String requestResubmission(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        Submission submission = submissionRepository.findById(id).orElseThrow();
        submission.setStatus(SubmissionStatus.RESUBMISSION_REQUESTED);
        submission.setFeedback((submission.getFeedback() == null ? "" : submission.getFeedback() + "\n\n")
                + "Resubmission requested: please revise according to the feedback and submit again.");
        submissionRepository.save(submission);
        return "redirect:/assignments/" + submission.getAssignment().getId();
    }
}
