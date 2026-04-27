package com.example.academicfeedback.config;

import com.example.academicfeedback.model.Assignment;
import com.example.academicfeedback.model.CapstoneEvaluation;
import com.example.academicfeedback.model.CapstoneMilestone;
import com.example.academicfeedback.model.CapstoneProject;
import com.example.academicfeedback.model.CapstoneStatus;
import com.example.academicfeedback.model.Course;
import com.example.academicfeedback.model.CourseMaterial;
import com.example.academicfeedback.model.Curriculum;
import com.example.academicfeedback.model.CurriculumStatus;
import com.example.academicfeedback.model.EngagementRecord;
import com.example.academicfeedback.model.FaqItem;
import com.example.academicfeedback.model.MaterialType;
import com.example.academicfeedback.model.MilestoneStatus;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.Submission;
import com.example.academicfeedback.model.SubmissionStatus;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.AssignmentRepository;
import com.example.academicfeedback.repository.CapstoneEvaluationRepository;
import com.example.academicfeedback.repository.CapstoneMilestoneRepository;
import com.example.academicfeedback.repository.CapstoneProjectRepository;
import com.example.academicfeedback.repository.CourseMaterialRepository;
import com.example.academicfeedback.repository.CourseRepository;
import com.example.academicfeedback.repository.CurriculumRepository;
import com.example.academicfeedback.repository.EngagementRecordRepository;
import com.example.academicfeedback.repository.FaqItemRepository;
import com.example.academicfeedback.repository.SubmissionRepository;
import com.example.academicfeedback.repository.UserAccountRepository;
import com.example.academicfeedback.service.AiFeedbackService;
import com.example.academicfeedback.service.GradingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final CourseRepository courseRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CurriculumRepository curriculumRepository;
    private final FaqItemRepository faqItemRepository;
    private final EngagementRecordRepository engagementRecordRepository;
    private final CapstoneProjectRepository capstoneProjectRepository;
    private final CapstoneMilestoneRepository capstoneMilestoneRepository;
    private final CapstoneEvaluationRepository capstoneEvaluationRepository;
    private final GradingService gradingService;
    private final AiFeedbackService aiFeedbackService;

    public DataInitializer(UserAccountRepository userAccountRepository,
                           CourseRepository courseRepository,
                           CourseMaterialRepository courseMaterialRepository,
                           AssignmentRepository assignmentRepository,
                           SubmissionRepository submissionRepository,
                           CurriculumRepository curriculumRepository,
                           FaqItemRepository faqItemRepository,
                           EngagementRecordRepository engagementRecordRepository,
                           CapstoneProjectRepository capstoneProjectRepository,
                           CapstoneMilestoneRepository capstoneMilestoneRepository,
                           CapstoneEvaluationRepository capstoneEvaluationRepository,
                           GradingService gradingService,
                           AiFeedbackService aiFeedbackService) {
        this.userAccountRepository = userAccountRepository;
        this.courseRepository = courseRepository;
        this.courseMaterialRepository = courseMaterialRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.curriculumRepository = curriculumRepository;
        this.faqItemRepository = faqItemRepository;
        this.engagementRecordRepository = engagementRecordRepository;
        this.capstoneProjectRepository = capstoneProjectRepository;
        this.capstoneMilestoneRepository = capstoneMilestoneRepository;
        this.capstoneEvaluationRepository = capstoneEvaluationRepository;
        this.gradingService = gradingService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @Override
    public void run(String... args) {
        if (userAccountRepository.count() > 0) {
            return;
        }

        UserAccount admin = userAccountRepository.save(new UserAccount(
                "System Admin", "admin@demo.com", "1234", Role.ADMIN, "Administration"));
        UserAccount faculty = userAccountRepository.save(new UserAccount(
                "Dr. Nusrat Jahan", "faculty@demo.com", "1234", Role.FACULTY, "Computer Science"));
        UserAccount student = userAccountRepository.save(new UserAccount(
                "Rahim Uddin", "student@demo.com", "1234", Role.STUDENT, "Computer Science"));

        Course course = courseRepository.save(new Course(
                "CSE-320",
                "Software Engineering",
                "Spring 2026",
                "Requirements, design, testing, project management, and quality assurance.",
                faculty));

        courseMaterialRepository.save(new CourseMaterial(
                "Week 1 Lecture Note",
                MaterialType.NOTE,
                "https://example.com/week-1-note",
                course));
        courseMaterialRepository.save(new CourseMaterial(
                "Assessment Rubric",
                MaterialType.LINK,
                "https://example.com/rubric",
                course));

        Assignment assignment = assignmentRepository.save(new Assignment(
                "Requirement Analysis Report",
                "Write a short SRS for an academic feedback system. Include users, features, constraints, and acceptance criteria.",
                LocalDate.now().plusDays(7),
                100,
                course));

        Submission submission = new Submission(
                assignment,
                student,
                "The system will help faculty collect submissions, give marks, generate feedback, and monitor student performance.");
        submission.setMarks(78.0);
        submission.setGrade(gradingService.calculateGrade(submission.getMarks(), assignment.getMaxMarks()));
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setFeedback(aiFeedbackService.draftFeedbackLocal(submission));
        submissionRepository.save(submission);

        EngagementRecord engagementRecord = new EngagementRecord();
        engagementRecord.setCourse(course);
        engagementRecord.setStudent(student);
        engagementRecord.setAttendancePercentage(88.0);
        engagementRecord.setParticipationScore(76.0);
        engagementRecord.setLmsActivityScore(82.0);
        engagementRecord.setRiskLevel("Low");
        engagementRecord.setRemarks("Consistent participation with room for deeper discussion in lab sessions.");
        engagementRecordRepository.save(engagementRecord);

        Curriculum curriculum = new Curriculum();
        curriculum.setProgramName("B.Sc. in Computer Science");
        curriculum.setCourseCode("CSE-320");
        curriculum.setCourseTitle("Software Engineering");
        curriculum.setCreditHours(3);
        curriculum.setVersionName("Version 1.0");
        curriculum.setEffectiveFrom(LocalDate.now());
        curriculum.setStatus(CurriculumStatus.UNDER_REVIEW);
        curriculum.setCourseLearningOutcomes("CLO1: Analyze software requirements.\nCLO2: Design maintainable systems.\nCLO3: Apply testing and quality assurance practices.");
        curriculum.setProgramLearningOutcomes("PLO1: Problem analysis.\nPLO2: Design and development.\nPLO3: Modern tool usage.");
        curriculum.setIndustryAlignment("Covers agile planning, documentation, testing, and project quality metrics.");
        curriculum.setPeerBenchmarking("Compared with common software engineering course topics: requirements, UML, testing, DevOps basics, and project management.");
        curriculum.setEmergingTechnologyTrends("AI-assisted assessment, learning analytics, secure software delivery, and cloud-native deployment.");
        curriculum.setStakeholderFeedback("Students requested more practical rubric examples and capstone-style templates.");
        curriculum.setRevisionProposal("Add AI-assisted feedback workflow, rubric-based evaluation, and analytics dashboard activity.");
        curriculum.setVersionHistory("2026: Initial AI-enabled academic feedback version created.");
        curriculumRepository.save(curriculum);

        FaqItem faqItem = new FaqItem();
        faqItem.setQuestion("How can I request a recheck after marks are published?");
        faqItem.setAnswer("Open the assignment details page, review your feedback, write a short reason, and submit the recheck request.");
        faqItem.setCategory("Assessment");
        faqItem.setCourse(course);
        faqItem.setCreatedBy(faculty);
        faqItemRepository.save(faqItem);

        CapstoneProject project = new CapstoneProject();
        project.setTitle("AI-driven Academic Feedback System");
        project.setProposalSummary("A web platform that helps faculty manage courses, submissions, assessment, feedback, curriculum mapping, and capstone tracking.");
        project.setTeamMembers("Rahim Uddin, Samia Akter");
        project.setTechnologyStack("Java, Spring Boot, Thymeleaf, H2/MySQL, OpenAI Responses API");
        project.setOriginalityScore(92.0);
        project.setStatus(CapstoneStatus.IN_PROGRESS);
        project.setSubmittedBy(student);
        project.setSupervisor(faculty);
        project.setAiScreening("Local sample screening: clear scope, relevant academic workflow, and manageable implementation plan.");
        capstoneProjectRepository.save(project);

        CapstoneMilestone milestone = new CapstoneMilestone();
        milestone.setTitle("Proposal Review");
        milestone.setDueDate(LocalDate.now().plusDays(10));
        milestone.setDeliverable("Proposal document, scope list, ER diagram, and UI wireframe.");
        milestone.setFeedback("Prepare a clearer database relationship diagram before the next review.");
        milestone.setStatus(MilestoneStatus.REVIEWED);
        milestone.setProject(project);
        capstoneMilestoneRepository.save(milestone);

        CapstoneEvaluation evaluation = new CapstoneEvaluation();
        evaluation.setRubricName("Proposal Defense");
        evaluation.setScore(82.0);
        evaluation.setMaxScore(100.0);
        evaluation.setComments("Strong academic relevance with a clear Java Spring Boot implementation path.");
        evaluation.setAiFeedback("Local sample feedback: improve originality evidence, add risk analysis, and prepare milestone-based progress reports.");
        evaluation.setEvaluator(faculty);
        evaluation.setProject(project);
        capstoneEvaluationRepository.save(evaluation);

        admin.setActive(true);
        userAccountRepository.save(admin);
    }
}
