package com.example.ms_recursos_innovatech.service;

import com.example.ms_recursos_innovatech.dto.AvailabilityRequestDTO;
import com.example.ms_recursos_innovatech.dto.AvailabilityResponseDTO;
import com.example.ms_recursos_innovatech.model.Availability;
import com.example.ms_recursos_innovatech.model.Professional;
import com.example.ms_recursos_innovatech.repository.AvailabilityRepository;
import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final ProfessionalRepository professionalRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository,
            ProfessionalRepository professionalRepository) {
        this.availabilityRepository = availabilityRepository;
        this.professionalRepository = professionalRepository;
    }

    public List<AvailabilityResponseDTO> findAll() {
        return availabilityRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AvailabilityResponseDTO findById(Long id) {
        return availabilityRepository.findById(id)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public AvailabilityResponseDTO save(AvailabilityRequestDTO requestDTO) {
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        if (professional == null) {
            return null;
        }
        Availability availability = new Availability();
        availability.setProfessional(professional);
        availability.setDateFrom(requestDTO.getDateFrom());
        availability.setDateTo(requestDTO.getDateTo());
        availability.setAvailableHours(requestDTO.getAvailableHours());
        availability.setAvailabilityType(requestDTO.getAvailabilityType());
        availability.setNotes(requestDTO.getNotes());
        LocalDateTime now = LocalDateTime.now();
        availability.setCreatedAt(now);
        availability.setUpdatedAt(now);
        Availability saved = availabilityRepository.save(availability);
        return toResponseDTO(saved);
    }

    public AvailabilityResponseDTO update(Long id, AvailabilityRequestDTO requestDTO) {
        Availability existing = availabilityRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        if (professional == null) {
            return null;
        }
        existing.setProfessional(professional);
        existing.setDateFrom(requestDTO.getDateFrom());
        existing.setDateTo(requestDTO.getDateTo());
        existing.setAvailableHours(requestDTO.getAvailableHours());
        existing.setAvailabilityType(requestDTO.getAvailabilityType());
        existing.setNotes(requestDTO.getNotes());
        existing.setUpdatedAt(LocalDateTime.now());
        Availability saved = availabilityRepository.save(existing);
        return toResponseDTO(saved);
    }

    public boolean delete(Long id) {
        if (!availabilityRepository.existsById(id)) {
            return false;
        }
        availabilityRepository.deleteById(id);
        return true;
    }

    public List<AvailabilityResponseDTO> findByResourceId(Long resourceId) {
        return availabilityRepository.findByProfessionalResourceId(resourceId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AvailabilityResponseDTO> findByAvailabilityType(String availabilityType) {
        return availabilityRepository.findByAvailabilityType(availabilityType).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private AvailabilityResponseDTO toResponseDTO(Availability availability) {
        Long resourceId = availability.getProfessional() != null
                ? availability.getProfessional().getResourceId()
                : null;
        return new AvailabilityResponseDTO(
                availability.getAvailabilityId(),
                resourceId,
                availability.getDateFrom(),
                availability.getDateTo(),
                availability.getAvailableHours(),
                availability.getAvailabilityType(),
                availability.getNotes(),
                availability.getCreatedAt(),
                availability.getUpdatedAt());
    }
}
