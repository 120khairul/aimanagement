package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.CapstoneEvaluation;
import com.example.academicfeedback.model.CapstoneMilestone;
import com.example.academicfeedback.model.CapstoneProject;
import com.example.academicfeedback.model.CapstoneStatus;
import com.example.academicfeedback.model.MilestoneStatus;
import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.CapstoneEvaluationRepository;
import com.example.academicfeedback.repository.CapstoneMilestoneRepository;
import com.example.academicfeedback.repository.CapstoneProjectRepository;
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

import java.time.LocalDateTime;

@Controller
public class CapstoneController {

    private final CapstoneProjectRepository capstoneProjectRepository;
    private final CapstoneMilestoneRepository capstoneMilestoneRepository;
    private final CapstoneEvaluationRepository capstoneEvaluationRepository;
    private final UserAccountRepository userAccountRepository;
    private final SessionService sessionService;
    private final AiFeedbackService aiFeedbackService;

    public CapstoneController(CapstoneProjectRepository capstoneProjectRepository,
                              CapstoneMilestoneRepository capstoneMilestoneRepository,
                              CapstoneEvaluationRepository capstoneEvaluationRepository,
                              UserAccountRepository userAccountRepository,
                              SessionService sessionService,
                              AiFeedbackService aiFeedbackService) {
        this.capstoneProjectRepository = capstoneProjectRepository;
        this.capstoneMilestoneRepository = capstoneMilestoneRepository;
        this.capstoneEvaluationRepository = capstoneEvaluationRepository;
        this.userAccountRepository = userAccountRepository;
        this.sessionService = sessionService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @GetMapping("/capstones")
    public String capstones(HttpSession session, Model model) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() == Role.STUDENT) {
            model.addAttribute("projects", capstoneProjectRepository.findBySubmittedById(user.getId()));
        } else if (user.getRole() == Role.FACULTY) {
            model.addAttribute("projects",
                    capstoneProjectRepository.findBySupervisorIdOrCoSupervisorId(user.getId(), user.getId()));
        } else {
            model.addAttribute("projects", capstoneProjectRepository.findAll());
        }
        return "capstones/index";
    }

    @GetMapping("/capstones/new")
    public String newCapstone(HttpSession session, Model model) {
        if (sessionService.currentUser(session).isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("project", new CapstoneProject());
        addPeople(model);
        model.addAttribute("formAction", "/capstones");
        return "capstones/form";
    }

    @PostMapping("/capstones")
    public String createCapstone(@ModelAttribute CapstoneProject project,
                                 @RequestParam(required = false) Long supervisorId,
                                 @RequestParam(required = false) Long coSupervisorId,
                                 HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        project.setSubmittedBy(user);
        userAccountRepository.findById(supervisorId == null ? -1 : supervisorId).ifPresent(project::setSupervisor);
        userAccountRepository.findById(coSupervisorId == null ? -1 : coSupervisorId).ifPresent(project::setCoSupervisor);
        capstoneProjectRepository.save(project);
        return "redirect:/capstones";
    }

    @GetMapping("/capstones/{id}")
    public String capstoneDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (sessionService.currentUser(session).isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("project", capstoneProjectRepository.findById(id).orElseThrow());
        model.addAttribute("milestone", new CapstoneMilestone());
        model.addAttribute("evaluation", new CapstoneEvaluation());
        return "capstones/details";
    }

    @GetMapping("/capstones/{id}/edit")
    public String editCapstone(@PathVariable Long id, HttpSession session, Model model) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        CapstoneProject project = capstoneProjectRepository.findById(id).orElseThrow();
        if (!canManageProject(user, project)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("project", project);
        addPeople(model);
        model.addAttribute("formAction", "/capstones/" + id);
        return "capstones/form";
    }

    @PostMapping("/capstones/{id}")
    public String updateCapstone(@PathVariable Long id,
                                 @ModelAttribute CapstoneProject form,
                                 @RequestParam(required = false) Long supervisorId,
                                 @RequestParam(required = false) Long coSupervisorId,
                                 HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        CapstoneProject project = capstoneProjectRepository.findById(id).orElseThrow();
        if (!canManageProject(user, project)) {
            return "redirect:/dashboard";
        }

        project.setTitle(form.getTitle());
        project.setProposalSummary(form.getProposalSummary());
        project.setTeamMembers(form.getTeamMembers());
        project.setTechnologyStack(form.getTechnologyStack());
        project.setOriginalityScore(form.getOriginalityScore());
        project.setStatus(form.getStatus());
        project.setSupervisor(null);
        project.setCoSupervisor(null);
        userAccountRepository.findById(supervisorId == null ? -1 : supervisorId).ifPresent(project::setSupervisor);
        userAccountRepository.findById(coSupervisorId == null ? -1 : coSupervisorId).ifPresent(project::setCoSupervisor);
        capstoneProjectRepository.save(project);
        return "redirect:/capstones/" + id;
    }

    @PostMapping("/capstones/{id}/ai-screen")
    public String aiScreen(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        CapstoneProject project = capstoneProjectRepository.findById(id).orElseThrow();
        project.setAiScreening(aiFeedbackService.capstoneProposalScreening(projectContext(project)));
        capstoneProjectRepository.save(project);
        return "redirect:/capstones/" + id;
    }

    @PostMapping("/capstones/{id}/ai-report")
    public String aiReport(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        CapstoneProject project = capstoneProjectRepository.findById(id).orElseThrow();
        project.setFinalReportSummary(aiFeedbackService.capstoneEvaluationFeedback(projectContext(project)
                + "\nMilestones: " + project.getMilestones().size()
                + "\nEvaluations: " + project.getEvaluations().size()));
        capstoneProjectRepository.save(project);
        return "redirect:/capstones/" + id;
    }

    @PostMapping("/capstones/{id}/milestones")
    public String addMilestone(@PathVariable Long id,
                               @ModelAttribute CapstoneMilestone milestone,
                               HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        milestone.setProject(capstoneProjectRepository.findById(id).orElseThrow());
        capstoneMilestoneRepository.save(milestone);
        return "redirect:/capstones/" + id;
    }

    @PostMapping("/capstones/milestones/{id}")
    public String updateMilestone(@PathVariable Long id,
                                  @RequestParam MilestoneStatus status,
                                  @RequestParam(required = false) String deliverable,
                                  @RequestParam(required = false) String feedback,
                                  HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        CapstoneMilestone milestone = capstoneMilestoneRepository.findById(id).orElseThrow();
        CapstoneProject project = milestone.getProject();
        if (!canManageProject(user, project)) {
            return "redirect:/dashboard";
        }
        milestone.setStatus(status);
        milestone.setDeliverable(deliverable);
        milestone.setFeedback(feedback);
        capstoneMilestoneRepository.save(milestone);
        return "redirect:/capstones/" + project.getId();
    }

    @PostMapping("/capstones/{id}/evaluations")
    public String addEvaluation(@PathVariable Long id,
                                @ModelAttribute CapstoneEvaluation evaluation,
                                @RequestParam(defaultValue = "false") boolean generateWithAi,
                                HttpSession session) {
        UserAccount user = sessionService.currentUser(session).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        CapstoneProject project = capstoneProjectRepository.findById(id).orElseThrow();
        evaluation.setProject(project);
        evaluation.setEvaluator(user);
        evaluation.setEvaluatedAt(LocalDateTime.now());
        if (generateWithAi) {
            evaluation.setAiFeedback(aiFeedbackService.capstoneEvaluationFeedback(
                    projectContext(project)
                            + "\nRubric: " + evaluation.getRubricName()
                            + "\nScore: " + evaluation.getScore() + "/" + evaluation.getMaxScore()
                            + "\nComments: " + evaluation.getComments()));
        }
        capstoneEvaluationRepository.save(evaluation);
        return "redirect:/capstones/" + id;
    }

    @PostMapping("/capstones/{id}/delete")
    public String deleteCapstone(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN, Role.FACULTY)) {
            return "redirect:/dashboard";
        }
        capstoneProjectRepository.deleteById(id);
        return "redirect:/capstones";
    }

    private void addPeople(Model model) {
        model.addAttribute("facultyUsers", userAccountRepository.findByRole(Role.FACULTY));
    }

    private boolean canManageProject(UserAccount user, CapstoneProject project) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (project.getSubmittedBy() != null && project.getSubmittedBy().getId().equals(user.getId())) {
            return true;
        }
        if (project.getSupervisor() != null && project.getSupervisor().getId().equals(user.getId())) {
            return true;
        }
        return project.getCoSupervisor() != null && project.getCoSupervisor().getId().equals(user.getId());
    }

    private String projectContext(CapstoneProject project) {
        String supervisor = project.getSupervisor() == null ? "Unassigned" : project.getSupervisor().getFullName();
        return "Title: " + project.getTitle()
                + "\nStatus: " + project.getStatus().getDisplayName()
                + "\nTeam: " + project.getTeamMembers()
                + "\nSupervisor: " + supervisor
                + "\nTechnology stack: " + project.getTechnologyStack()
                + "\nOriginality score: " + project.getOriginalityScore()
                + "\nProposal: " + project.getProposalSummary();
    }
}
