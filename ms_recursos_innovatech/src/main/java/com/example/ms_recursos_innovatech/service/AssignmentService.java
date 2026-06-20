package com.example.ms_recursos_innovatech.service;

import com.example.ms_recursos_innovatech.dto.AssignmentRequestDTO;
import com.example.ms_recursos_innovatech.dto.AssignmentResponseDTO;
import com.example.ms_recursos_innovatech.model.Assignment;
import com.example.ms_recursos_innovatech.model.Professional;
import com.example.ms_recursos_innovatech.repository.AssignmentRepository;
import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ProfessionalRepository professionalRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
            ProfessionalRepository professionalRepository) {
        this.assignmentRepository = assignmentRepository;
        this.professionalRepository = professionalRepository;
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
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        if (professional == null) {
            return null;
        }
        Assignment assignment = new Assignment();
        assignment.setProfessional(professional);
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
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        if (professional == null) {
            return null;
        }
        existing.setProfessional(professional);
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
        return assignmentRepository.findByProfessionalResourceId(resourceId).stream()
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
        Long resourceId = assignment.getProfessional() != null
                ? assignment.getProfessional().getResourceId()
                : null;
        return new AssignmentResponseDTO(
                assignment.getAssignmentId(),
                resourceId,
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
