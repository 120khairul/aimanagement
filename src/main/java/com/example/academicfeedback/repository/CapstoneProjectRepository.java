package com.example.academicfeedback.repository;

import com.example.academicfeedback.model.CapstoneProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapstoneProjectRepository extends JpaRepository<CapstoneProject, Long> {

    List<CapstoneProject> findBySubmittedById(Long submittedById);

    List<CapstoneProject> findBySupervisorIdOrCoSupervisorId(Long supervisorId, Long coSupervisorId);
}
