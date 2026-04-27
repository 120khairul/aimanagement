package com.example.academicfeedback.repository;

import com.example.academicfeedback.model.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqItemRepository extends JpaRepository<FaqItem, Long> {

    List<FaqItem> findByCourseId(Long courseId);
}
