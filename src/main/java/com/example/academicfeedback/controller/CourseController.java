package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.Course;
import com.example.academicfeedback.model.CourseMaterial;
import com.example.academicfeedback.model.EngagementRecord;
import com.example.academicfeedback.model.MaterialType;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.CourseMaterialRepository;
import com.example.academicfeedback.repository.CourseRepository;
import com.example.academicfeedback.repository.EngagementRecordRepository;
import com.example.academicfeedback.repository.UserAccountRepository;
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
public class CourseController {

    private final CourseRepository courseRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final EngagementRecordRepository engagementRecordRepository;
    private final UserAccountRepository userAccountRepository;
    private final SessionService sessionService;
    private final AiFeedbackService aiFeedbackService;

    public CourseController(CourseRepository courseRepository,
                            CourseMaterialRepository courseMaterialRepository,
                            EngagementRecordRepository engagementRecordRepository,
                            UserAccountRepository userAccountRepository,
                            SessionService sessionService,
                            AiFeedbackService aiFeedbackService) {
        this.courseRepository = courseRepository;
        this.courseMaterialRepository = courseMaterialRepository;
        this.engagementRecordRepository = engagementRecordRepository;
        this.userAccountRepository = userAccountRepository;
        this.sessionService = sessionService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @GetMapping("/courses")
    public String courses(HttpSession session, Model model) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() == Role.FACULTY) {
            model.addAttribute("courses", courseRepository.findByTeacherId(user.getId()));
        } else {
            model.addAttribute("courses", courseRepository.findAll());
        }
        return "courses/index";
    }

    @GetMapping("/courses/new")
    public String newCourse(HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("course", new Course());
        model.addAttribute("teachers", userAccountRepository.findByRole(Role.FACULTY));
        model.addAttribute("formAction", "/courses");
        return "courses/form";
    }

    @PostMapping("/courses")
    public String createCourse(@ModelAttribute Course course,
                               @RequestParam(required = false) Long teacherId,
                               HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.FACULTY)) {
            return "redirect:/dashboard";
        }

        UserAccount teacher = user;
        if (user.getRole() == Role.ADMIN && teacherId != null) {
            teacher = userAccountRepository.findById(teacherId).orElse(user);
        }
        course.setTeacher(teacher);
        courseRepository.save(course);
        return "redirect:/courses";
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourse(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("course", courseRepository.findById(id).orElseThrow());
        model.addAttribute("teachers", userAccountRepository.findByRole(Role.FACULTY));
        model.addAttribute("formAction", "/courses/" + id);
        return "courses/form";
    }

    @PostMapping("/courses/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute Course form,
                               @RequestParam(required = false) Long teacherId,
                               HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.FACULTY)) {
            return "redirect:/dashboard";
        }

        Course course = courseRepository.findById(id).orElseThrow();
        course.setCode(form.getCode());
        course.setTitle(form.getTitle());
        course.setSemester(form.getSemester());
        course.setDescription(form.getDescription());
        if (user.getRole() == Role.ADMIN && teacherId != null) {
            course.setTeacher(userAccountRepository.findById(teacherId).orElse(course.getTeacher()));
        }
        courseRepository.save(course);
        return "redirect:/courses/" + id;
    }

    @GetMapping("/courses/{id}")
    public String courseDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (sessionService.currentUser(session).isEmpty()) {
            return "redirect:/login";
        }

        Course course = courseRepository.findById(id).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("material", new CourseMaterial());
        model.addAttribute("engagementRecords", engagementRecordRepository.findByCourseId(id));
        model.addAttribute("students", userAccountRepository.findByRole(Role.STUDENT));
        return "courses/details";
    }

    @PostMapping("/courses/{id}/ai-quiz")
    public String generateQuizSuggestions(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        Course course = courseRepository.findById(id).orElseThrow();
        course.setAiQuizSuggestions(aiFeedbackService.suggestQuizQuestions(course));
        courseRepository.save(course);
        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/materials")
    public String addMaterial(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam MaterialType materialType,
                              @RequestParam String resourceLink,
                              HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        Course course = courseRepository.findById(id).orElseThrow();
        courseMaterialRepository.save(new CourseMaterial(title, materialType, resourceLink, course));
        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/engagement")
    public String saveEngagement(@PathVariable Long id,
                                 @RequestParam Long studentId,
                                 @RequestParam(required = false) Double attendancePercentage,
                                 @RequestParam(required = false) Double participationScore,
                                 @RequestParam(required = false) Double lmsActivityScore,
                                 @RequestParam String riskLevel,
                                 @RequestParam(required = false) String remarks,
                                 HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        EngagementRecord record = new EngagementRecord();
        record.setCourse(courseRepository.findById(id).orElseThrow());
        record.setStudent(userAccountRepository.findById(studentId).orElseThrow());
        record.setAttendancePercentage(attendancePercentage);
        record.setParticipationScore(participationScore);
        record.setLmsActivityScore(lmsActivityScore);
        record.setRiskLevel(riskLevel);
        record.setRemarks(remarks);
        engagementRecordRepository.save(record);
        return "redirect:/courses/" + id;
    }

    @PostMapping("/materials/{id}/delete")
    public String deleteMaterial(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        CourseMaterial material = courseMaterialRepository.findById(id).orElseThrow();
        Long courseId = material.getCourse().getId();
        courseMaterialRepository.deleteById(id);
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        courseRepository.deleteById(id);
        return "redirect:/courses";
    }
}
