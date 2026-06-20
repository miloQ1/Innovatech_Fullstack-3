package com.example.ms_recursos_innovatech.service;

import com.example.ms_recursos_innovatech.dto.AbsenceRequestDTO;
import com.example.ms_recursos_innovatech.dto.AbsenceResponseDTO;
import com.example.ms_recursos_innovatech.model.Absence;
import com.example.ms_recursos_innovatech.model.Professional;
import com.example.ms_recursos_innovatech.repository.AbsenceRepository;
import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final ProfessionalRepository professionalRepository;

    public AbsenceService(AbsenceRepository absenceRepository, ProfessionalRepository professionalRepository) {
        this.absenceRepository = absenceRepository;
        this.professionalRepository = professionalRepository;
    }

    public List<AbsenceResponseDTO> findAll() {
        return absenceRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AbsenceResponseDTO findById(Long id) {
        return absenceRepository.findById(id)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public AbsenceResponseDTO save(AbsenceRequestDTO requestDTO) {
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        if (professional == null) {
            return null;
        }
        Absence absence = new Absence();
        absence.setProfessional(professional);
        absence.setAbsenceType(requestDTO.getAbsenceType());
        absence.setStartDate(requestDTO.getStartDate());
        absence.setEndDate(requestDTO.getEndDate());
        absence.setHoursAffected(requestDTO.getHoursAffected());
        absence.setReason(requestDTO.getReason());
        LocalDateTime now = LocalDateTime.now();
        absence.setCreatedAt(now);
        absence.setUpdatedAt(now);
        Absence saved = absenceRepository.save(absence);
        return toResponseDTO(saved);
    }

    public AbsenceResponseDTO update(Long id, AbsenceRequestDTO requestDTO) {
        Absence existing = absenceRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        if (professional == null) {
            return null;
        }
        existing.setProfessional(professional);
        existing.setAbsenceType(requestDTO.getAbsenceType());
        existing.setStartDate(requestDTO.getStartDate());
        existing.setEndDate(requestDTO.getEndDate());
        existing.setHoursAffected(requestDTO.getHoursAffected());
        existing.setReason(requestDTO.getReason());
        existing.setUpdatedAt(LocalDateTime.now());
        Absence saved = absenceRepository.save(existing);
        return toResponseDTO(saved);
    }

    public boolean delete(Long id) {
        if (!absenceRepository.existsById(id)) {
            return false;
        }
        absenceRepository.deleteById(id);
        return true;
    }

    public List<AbsenceResponseDTO> findByResourceId(Long resourceId) {
        return absenceRepository.findByProfessionalResourceId(resourceId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AbsenceResponseDTO> findByAbsenceType(String absenceType) {
        return absenceRepository.findByAbsenceType(absenceType).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private AbsenceResponseDTO toResponseDTO(Absence absence) {
        Long resourceId = absence.getProfessional() != null ? absence.getProfessional().getResourceId() : null;
        return new AbsenceResponseDTO(
                absence.getAbsenceId(),
                resourceId,
                absence.getAbsenceType(),
                absence.getStartDate(),
                absence.getEndDate(),
                absence.getHoursAffected(),
                absence.getReason(),
                absence.getCreatedAt(),
                absence.getUpdatedAt());
    }
}
