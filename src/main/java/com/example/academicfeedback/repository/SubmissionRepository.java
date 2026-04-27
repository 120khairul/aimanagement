package com.example.academicfeedback.repository;

import com.example.academicfeedback.model.Submission;
import com.example.academicfeedback.model.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssignmentId(Long assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<Submission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);

    List<Submission> findByAssignment_Course_Teacher_IdOrderBySubmittedAtDesc(Long teacherId);

    long countByStatus(SubmissionStatus status);

    @Query("select avg((s.marks * 100.0) / s.assignment.maxMarks) from Submission s where s.marks is not null")
    Double averagePercentage();
}
