package com.example.ms_recursos_innovatech.service;

import com.example.ms_recursos_innovatech.dto.ResourceSkillRequestDTO;
import com.example.ms_recursos_innovatech.dto.ResourceSkillResponseDTO;
import com.example.ms_recursos_innovatech.model.Professional;
import com.example.ms_recursos_innovatech.model.ResourceSkill;
import com.example.ms_recursos_innovatech.model.Skill;
import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;
import com.example.ms_recursos_innovatech.repository.ResourceSkillRepository;
import com.example.ms_recursos_innovatech.repository.SkillRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ResourceSkillService {

    private final ResourceSkillRepository resourceSkillRepository;
    private final ProfessionalRepository professionalRepository;
    private final SkillRepository skillRepository;

    public ResourceSkillService(ResourceSkillRepository resourceSkillRepository,
            ProfessionalRepository professionalRepository, SkillRepository skillRepository) {
        this.resourceSkillRepository = resourceSkillRepository;
        this.professionalRepository = professionalRepository;
        this.skillRepository = skillRepository;
    }

    public List<ResourceSkillResponseDTO> findAll() {
        return resourceSkillRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ResourceSkillResponseDTO findById(Long id) {
        return resourceSkillRepository.findById(id)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public ResourceSkillResponseDTO save(ResourceSkillRequestDTO requestDTO) {
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        Skill skill = skillRepository.findById(requestDTO.getSkillId()).orElse(null);
        if (professional == null || skill == null) {
            return null;
        }
        ResourceSkill resourceSkill = new ResourceSkill();
        resourceSkill.setProfessional(professional);
        resourceSkill.setSkill(skill);
        resourceSkill.setProficiencyLevel(requestDTO.getProficiencyLevel());
        resourceSkill.setYearsExperience(requestDTO.getYearsExperience());
        resourceSkill.setCertified(requestDTO.getCertified());
        resourceSkill.setLastUpdated(LocalDateTime.now());
        ResourceSkill saved = resourceSkillRepository.save(resourceSkill);
        return toResponseDTO(saved);
    }

    public ResourceSkillResponseDTO update(Long id, ResourceSkillRequestDTO requestDTO) {
        ResourceSkill existing = resourceSkillRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        Professional professional = professionalRepository.findById(requestDTO.getResourceId()).orElse(null);
        Skill skill = skillRepository.findById(requestDTO.getSkillId()).orElse(null);
        if (professional == null || skill == null) {
            return null;
        }
        existing.setProfessional(professional);
        existing.setSkill(skill);
        existing.setProficiencyLevel(requestDTO.getProficiencyLevel());
        existing.setYearsExperience(requestDTO.getYearsExperience());
        existing.setCertified(requestDTO.getCertified());
        existing.setLastUpdated(LocalDateTime.now());
        ResourceSkill saved = resourceSkillRepository.save(existing);
        return toResponseDTO(saved);
    }

    public boolean delete(Long id) {
        if (!resourceSkillRepository.existsById(id)) {
            return false;
        }
        resourceSkillRepository.deleteById(id);
        return true;
    }

    public List<ResourceSkillResponseDTO> findByResourceId(Long resourceId) {
        return resourceSkillRepository.findByProfessionalResourceId(resourceId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ResourceSkillResponseDTO> findBySkillId(Long skillId) {
        return resourceSkillRepository.findBySkillSkillId(skillId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private ResourceSkillResponseDTO toResponseDTO(ResourceSkill resourceSkill) {
        Long resourceId = resourceSkill.getProfessional() != null
                ? resourceSkill.getProfessional().getResourceId()
                : null;
        Long skillId = resourceSkill.getSkill() != null ? resourceSkill.getSkill().getSkillId() : null;
        String skillName = resourceSkill.getSkill() != null ? resourceSkill.getSkill().getName() : null;
        return new ResourceSkillResponseDTO(
                resourceSkill.getResourceSkillId(),
                resourceId,
                skillId,
                skillName,
                resourceSkill.getProficiencyLevel(),
                resourceSkill.getYearsExperience(),
                resourceSkill.getCertified(),
                resourceSkill.getLastUpdated());
    }
}
