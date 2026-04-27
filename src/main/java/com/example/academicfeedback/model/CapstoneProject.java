package com.example.academicfeedback.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CapstoneProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Lob
    @Column(nullable = false)
    private String proposalSummary;

    private String teamMembers;

    private String technologyStack;

    private Double originalityScore;

    @Enumerated(EnumType.STRING)
    private CapstoneStatus status = CapstoneStatus.PROPOSED;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount coSupervisor;

    @Lob
    private String aiScreening;

    @Lob
    private String finalReportSummary;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CapstoneMilestone> milestones = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CapstoneEvaluation> evaluations = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProposalSummary() {
        return proposalSummary;
    }

    public void setProposalSummary(String proposalSummary) {
        this.proposalSummary = proposalSummary;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(String teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getTechnologyStack() {
        return technologyStack;
    }

    public void setTechnologyStack(String technologyStack) {
        this.technologyStack = technologyStack;
    }

    public Double getOriginalityScore() {
        return originalityScore;
    }

    public void setOriginalityScore(Double originalityScore) {
        this.originalityScore = originalityScore;
    }

    public CapstoneStatus getStatus() {
        return status;
    }

    public void setStatus(CapstoneStatus status) {
        this.status = status;
    }

    public UserAccount getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UserAccount submittedBy) {
        this.submittedBy = submittedBy;
    }

    public UserAccount getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(UserAccount supervisor) {
        this.supervisor = supervisor;
    }

    public UserAccount getCoSupervisor() {
        return coSupervisor;
    }

    public void setCoSupervisor(UserAccount coSupervisor) {
        this.coSupervisor = coSupervisor;
    }

    public String getAiScreening() {
        return aiScreening;
    }

    public void setAiScreening(String aiScreening) {
        this.aiScreening = aiScreening;
    }

    public String getFinalReportSummary() {
        return finalReportSummary;
    }

    public void setFinalReportSummary(String finalReportSummary) {
        this.finalReportSummary = finalReportSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CapstoneMilestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<CapstoneMilestone> milestones) {
        this.milestones = milestones;
    }

    public List<CapstoneEvaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<CapstoneEvaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
