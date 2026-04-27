package com.example.academicfeedback.repository;

import com.example.academicfeedback.model.EngagementRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngagementRecordRepository extends JpaRepository<EngagementRecord, Long> {

    List<EngagementRecord> findByCourseId(Long courseId);
}
