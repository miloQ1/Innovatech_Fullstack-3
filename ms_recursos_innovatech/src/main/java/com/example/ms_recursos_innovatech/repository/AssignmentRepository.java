package com.example.ms_recursos_innovatech.repository;

import com.example.ms_recursos_innovatech.model.Assignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByProfessionalResourceId(Long resourceId);

    List<Assignment> findByProjectId(Long projectId);

    List<Assignment> findByAssignmentStatus(String assignmentStatus);
}
