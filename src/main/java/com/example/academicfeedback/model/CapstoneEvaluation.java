package com.example.academicfeedback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
public class CapstoneEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String rubricName;

    private Double score;

    private Double maxScore = 100.0;

    @Lob
    private String comments;

    @Lob
    private String aiFeedback;

    private LocalDateTime evaluatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount evaluator;

    @ManyToOne(fetch = FetchType.LAZY)
    private CapstoneProject project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRubricName() {
        return rubricName;
    }

    public void setRubricName(String rubricName) {
        this.rubricName = rubricName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public UserAccount getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(UserAccount evaluator) {
        this.evaluator = evaluator;
    }

    public CapstoneProject getProject() {
        return project;
    }

    public void setProject(CapstoneProject project) {
        this.project = project;
    }
}
