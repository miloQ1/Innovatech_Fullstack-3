package com.example.ms_asignaciones.service;

import com.example.ms_asignaciones.dto.AssignmentRequestDTO;
import com.example.ms_asignaciones.dto.AssignmentResponseDTO;
import com.example.ms_asignaciones.model.Assignment;
import com.example.ms_asignaciones.repository.AssignmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<AssignmentResponseDTO> findAll() {
        return assignmentRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AssignmentResponseDTO findById(Long id) {
        return assignmentRepository.findById(id)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public AssignmentResponseDTO save(AssignmentRequestDTO requestDTO) {
        Assignment assignment = new Assignment();
        assignment.setResourceId(requestDTO.getResourceId());
        assignment.setProjectId(requestDTO.getProjectId());
        assignment.setProjectRole(requestDTO.getProjectRole());
        assignment.setAllocationPct(requestDTO.getAllocationPct());
        assignment.setPlannedHours(requestDTO.getPlannedHours());
        assignment.setStartDate(requestDTO.getStartDate());
        assignment.setEndDate(requestDTO.getEndDate());
        assignment.setAssignmentStatus(requestDTO.getAssignmentStatus());
        LocalDateTime now = LocalDateTime.now();
        assignment.setCreatedAt(now);
        assignment.setUpdatedAt(now);
        Assignment saved = assignmentRepository.save(assignment);
        return toResponseDTO(saved);
    }

    public AssignmentResponseDTO update(Long id, AssignmentRequestDTO requestDTO) {
        Assignment existing = assignmentRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setResourceId(requestDTO.getResourceId());
        existing.setProjectId(requestDTO.getProjectId());
        existing.setProjectRole(requestDTO.getProjectRole());
        existing.setAllocationPct(requestDTO.getAllocationPct());
        existing.setPlannedHours(requestDTO.getPlannedHours());
        existing.setStartDate(requestDTO.getStartDate());
        existing.setEndDate(requestDTO.getEndDate());
        existing.setAssignmentStatus(requestDTO.getAssignmentStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        Assignment saved = assignmentRepository.save(existing);
        return toResponseDTO(saved);
    }

    public boolean delete(Long id) {
        if (!assignmentRepository.existsById(id)) {
            return false;
        }
        assignmentRepository.deleteById(id);
        return true;
    }

    public List<AssignmentResponseDTO> findByResourceId(Long resourceId) {
        return assignmentRepository.findByResourceId(resourceId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AssignmentResponseDTO> findByProjectId(Long projectId) {
        return assignmentRepository.findByProjectId(projectId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AssignmentResponseDTO> findByAssignmentStatus(String assignmentStatus) {
        return assignmentRepository.findByAssignmentStatus(assignmentStatus).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private AssignmentResponseDTO toResponseDTO(Assignment assignment) {
        return new AssignmentResponseDTO(
                assignment.getAssignmentId(),
                assignment.getResourceId(),
                assignment.getProjectId(),
                assignment.getProjectRole(),
                assignment.getAllocationPct(),
                assignment.getPlannedHours(),
                assignment.getStartDate(),
                assignment.getEndDate(),
                assignment.getAssignmentStatus(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }
}
