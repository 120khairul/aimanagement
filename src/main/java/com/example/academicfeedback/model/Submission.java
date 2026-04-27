package com.example.academicfeedback.model;

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

import java.time.LocalDateTime;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount student;

    @Lob
    @Column(nullable = false)
    private String content;

    private LocalDateTime submittedAt = LocalDateTime.now();

    private Double marks;

    private String grade;

    @Lob
    private String feedback;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @Column(length = 1000)
    private String recheckReason;

    public Submission() {
    }

    public Submission(Assignment assignment, UserAccount student, String content) {
        this.assignment = assignment;
        this.student = student;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public UserAccount getStudent() {
        return student;
    }

    public void setStudent(UserAccount student) {
        this.student = student;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getRecheckReason() {
        return recheckReason;
    }

    public void setRecheckReason(String recheckReason) {
        this.recheckReason = recheckReason;
    }

    public double getPercentage() {
        if (marks == null || assignment == null || assignment.getMaxMarks() == null || assignment.getMaxMarks() == 0) {
            return 0;
        }
        return (marks / assignment.getMaxMarks()) * 100;
    }
}
