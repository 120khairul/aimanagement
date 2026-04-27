package com.example.academicfeedback.service;

import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.Submission;
import com.example.academicfeedback.model.SubmissionStatus;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.AssignmentRepository;
import com.example.academicfeedback.repository.CapstoneProjectRepository;
import com.example.academicfeedback.repository.CourseRepository;
import com.example.academicfeedback.repository.CurriculumRepository;
import com.example.academicfeedback.repository.FaqItemRepository;
import com.example.academicfeedback.repository.SubmissionRepository;
import com.example.academicfeedback.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final UserAccountRepository userAccountRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CurriculumRepository curriculumRepository;
    private final FaqItemRepository faqItemRepository;
    private final CapstoneProjectRepository capstoneProjectRepository;

    public DashboardService(UserAccountRepository userAccountRepository,
                            CourseRepository courseRepository,
                            AssignmentRepository assignmentRepository,
                            SubmissionRepository submissionRepository,
                            CurriculumRepository curriculumRepository,
                            FaqItemRepository faqItemRepository,
                            CapstoneProjectRepository capstoneProjectRepository) {
        this.userAccountRepository = userAccountRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.curriculumRepository = curriculumRepository;
        this.faqItemRepository = faqItemRepository;
        this.capstoneProjectRepository = capstoneProjectRepository;
    }

    public DashboardStats stats() {
        Double average = submissionRepository.averagePercentage();
        return new DashboardStats(
                userAccountRepository.count(),
                courseRepository.count(),
                assignmentRepository.count(),
                submissionRepository.count(),
                submissionRepository.countByStatus(SubmissionStatus.RECHECK_REQUESTED),
                curriculumRepository.count(),
                faqItemRepository.count(),
                capstoneProjectRepository.count(),
                average == null ? 0 : average
        );
    }

    public List<Submission> submissionsFor(UserAccount user) {
        if (user.getRole() == Role.STUDENT) {
            return submissionRepository.findByStudentIdOrderBySubmittedAtDesc(user.getId());
        }
        if (user.getRole() == Role.FACULTY) {
            return submissionRepository.findByAssignment_Course_Teacher_IdOrderBySubmittedAtDesc(user.getId());
        }
        return submissionRepository.findAll();
    }
}
