package com.example.ms_asignaciones.repository;

import com.example.ms_asignaciones.model.Assignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByResourceId(Long resourceId);

    List<Assignment> findByProjectId(Long projectId);

    List<Assignment> findByAssignmentStatus(String assignmentStatus);
}
