package com.example.academicfeedback.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank
    private String title;

    private String semester;

    @Column(length = 1200)
    private String description;

    @Lob
    private String aiQuizSuggestions;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseMaterial> materials = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EngagementRecord> engagementRecords = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaqItem> faqItems = new ArrayList<>();

    public Course() {
    }

    public Course(String code, String title, String semester, String description, UserAccount teacher) {
        this.code = code;
        this.title = title;
        this.semester = semester;
        this.description = description;
        this.teacher = teacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAiQuizSuggestions() {
        return aiQuizSuggestions;
    }

    public void setAiQuizSuggestions(String aiQuizSuggestions) {
        this.aiQuizSuggestions = aiQuizSuggestions;
    }

    public UserAccount getTeacher() {
        return teacher;
    }

    public void setTeacher(UserAccount teacher) {
        this.teacher = teacher;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<CourseMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<CourseMaterial> materials) {
        this.materials = materials;
    }

    public List<EngagementRecord> getEngagementRecords() {
        return engagementRecords;
    }

    public void setEngagementRecords(List<EngagementRecord> engagementRecords) {
        this.engagementRecords = engagementRecords;
    }

    public List<FaqItem> getFaqItems() {
        return faqItems;
    }

    public void setFaqItems(List<FaqItem> faqItems) {
        this.faqItems = faqItems;
    }
}
