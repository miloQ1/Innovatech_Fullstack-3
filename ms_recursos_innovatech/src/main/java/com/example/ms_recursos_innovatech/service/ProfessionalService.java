package com.example.ms_recursos_innovatech.service;

import com.example.ms_recursos_innovatech.dto.ProfessionalRequestDTO;
import com.example.ms_recursos_innovatech.dto.ProfessionalResponseDTO;
import com.example.ms_recursos_innovatech.model.Professional;
import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    public List<ProfessionalResponseDTO> findAll() {
        return professionalRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProfessionalResponseDTO findById(Long id) {
        return professionalRepository.findById(id)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public ProfessionalResponseDTO findByEmployeeCode(String employeeCode) {
        return professionalRepository.findByEmployeeCode(employeeCode)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public ProfessionalResponseDTO save(ProfessionalRequestDTO requestDTO) {
        Professional professional = new Professional();
        professional.setEmployeeCode(requestDTO.getEmployeeCode());
        applyRequest(professional, requestDTO);
        LocalDateTime now = LocalDateTime.now();
        professional.setCreatedAt(now);
        professional.setUpdatedAt(now);
        Professional saved = professionalRepository.save(professional);
        return toResponseDTO(saved);
    }

    public ProfessionalResponseDTO update(Long id, ProfessionalRequestDTO requestDTO) {
        Professional existing = professionalRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        applyRequest(existing, requestDTO);
        existing.setUpdatedAt(LocalDateTime.now());
        Professional saved = professionalRepository.save(existing);
        return toResponseDTO(saved);
    }

    public ProfessionalResponseDTO updateByEmployeeCode(String employeeCode, ProfessionalRequestDTO requestDTO) {
        Professional existing = professionalRepository.findByEmployeeCode(employeeCode).orElse(null);
        if (existing == null) {
            return null;
        }
        applyRequest(existing, requestDTO);
        existing.setUpdatedAt(LocalDateTime.now());
        Professional saved = professionalRepository.save(existing);
        return toResponseDTO(saved);
    }

    public boolean delete(Long id) {
        if (!professionalRepository.existsById(id)) {
            return false;
        }
        professionalRepository.deleteById(id);
        return true;
    }

    public List<ProfessionalResponseDTO> findByStatus(String status) {
        return professionalRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ProfessionalResponseDTO> findByRoleName(String roleName) {
        return professionalRepository.findByRoleName(roleName).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ProfessionalResponseDTO> findBySeniority(String seniority) {
        return professionalRepository.findBySeniority(seniority).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private void applyRequest(Professional professional, ProfessionalRequestDTO requestDTO) {
        professional.setFirstName(requestDTO.getFirstName());
        professional.setLastName(requestDTO.getLastName());
        professional.setEmail(requestDTO.getEmail());
        professional.setRoleName(requestDTO.getRoleName());
        professional.setSeniority(requestDTO.getSeniority());
        professional.setLocation(requestDTO.getLocation());
        professional.setTimeZone(requestDTO.getTimeZone());
        professional.setWeeklyCapacityHours(requestDTO.getWeeklyCapacityHours());
        professional.setStatus(requestDTO.getStatus());
    }

    private ProfessionalResponseDTO toResponseDTO(Professional professional) {
        return new ProfessionalResponseDTO(
                professional.getResourceId(),
                professional.getEmployeeCode(),
                professional.getFirstName(),
                professional.getLastName(),
                professional.getEmail(),
                professional.getRoleName(),
                professional.getSeniority(),
                professional.getLocation(),
                professional.getTimeZone(),
                professional.getWeeklyCapacityHours(),
                professional.getStatus(),
                professional.getCreatedAt(),
                professional.getUpdatedAt());
    }
}
