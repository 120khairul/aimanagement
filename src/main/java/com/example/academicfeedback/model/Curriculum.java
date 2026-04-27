package com.example.academicfeedback.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String programName;

    @NotBlank
    private String courseCode;

    @NotBlank
    private String courseTitle;

    @Min(1)
    private Integer creditHours = 3;

    private String versionName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate effectiveFrom;

    @Enumerated(EnumType.STRING)
    private CurriculumStatus status = CurriculumStatus.DRAFT;

    @Lob
    @Column(nullable = false)
    private String courseLearningOutcomes;

    @Lob
    private String programLearningOutcomes;

    @Lob
    private String industryAlignment;

    @Lob
    private String peerBenchmarking;

    @Lob
    private String emergingTechnologyTrends;

    @Lob
    private String stakeholderFeedback;

    @Lob
    private String revisionProposal;

    @Lob
    private String versionHistory;

    @Lob
    private String aiGapAnalysis;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Integer getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(Integer creditHours) {
        this.creditHours = creditHours;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public CurriculumStatus getStatus() {
        return status;
    }

    public void setStatus(CurriculumStatus status) {
        this.status = status;
    }

    public String getCourseLearningOutcomes() {
        return courseLearningOutcomes;
    }

    public void setCourseLearningOutcomes(String courseLearningOutcomes) {
        this.courseLearningOutcomes = courseLearningOutcomes;
    }

    public String getProgramLearningOutcomes() {
        return programLearningOutcomes;
    }

    public void setProgramLearningOutcomes(String programLearningOutcomes) {
        this.programLearningOutcomes = programLearningOutcomes;
    }

    public String getIndustryAlignment() {
        return industryAlignment;
    }

    public void setIndustryAlignment(String industryAlignment) {
        this.industryAlignment = industryAlignment;
    }

    public String getPeerBenchmarking() {
        return peerBenchmarking;
    }

    public void setPeerBenchmarking(String peerBenchmarking) {
        this.peerBenchmarking = peerBenchmarking;
    }

    public String getEmergingTechnologyTrends() {
        return emergingTechnologyTrends;
    }

    public void setEmergingTechnologyTrends(String emergingTechnologyTrends) {
        this.emergingTechnologyTrends = emergingTechnologyTrends;
    }

    public String getStakeholderFeedback() {
        return stakeholderFeedback;
    }

    public void setStakeholderFeedback(String stakeholderFeedback) {
        this.stakeholderFeedback = stakeholderFeedback;
    }

    public String getRevisionProposal() {
        return revisionProposal;
    }

    public void setRevisionProposal(String revisionProposal) {
        this.revisionProposal = revisionProposal;
    }

    public String getVersionHistory() {
        return versionHistory;
    }

    public void setVersionHistory(String versionHistory) {
        this.versionHistory = versionHistory;
    }

    public String getAiGapAnalysis() {
        return aiGapAnalysis;
    }

    public void setAiGapAnalysis(String aiGapAnalysis) {
        this.aiGapAnalysis = aiGapAnalysis;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
