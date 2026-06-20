package com.example.ms_recursos_innovatech.service;

import com.example.ms_recursos_innovatech.dto.SkillRequestDTO;
import com.example.ms_recursos_innovatech.dto.SkillResponseDTO;
import com.example.ms_recursos_innovatech.model.Skill;
import com.example.ms_recursos_innovatech.repository.SkillRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<SkillResponseDTO> findAll() {
        return skillRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public SkillResponseDTO findById(Long id) {
        return skillRepository.findById(id)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public SkillResponseDTO save(SkillRequestDTO requestDTO) {
        Skill skill = new Skill();
        applyRequest(skill, requestDTO);
        LocalDateTime now = LocalDateTime.now();
        skill.setCreatedAt(now);
        skill.setUpdatedAt(now);
        Skill saved = skillRepository.save(skill);
        return toResponseDTO(saved);
    }

    public SkillResponseDTO update(Long id, SkillRequestDTO requestDTO) {
        Skill existing = skillRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        applyRequest(existing, requestDTO);
        existing.setUpdatedAt(LocalDateTime.now());
        Skill saved = skillRepository.save(existing);
        return toResponseDTO(saved);
    }

    public boolean delete(Long id) {
        if (!skillRepository.existsById(id)) {
            return false;
        }
        skillRepository.deleteById(id);
        return true;
    }

    public List<SkillResponseDTO> findByStatus(String status) {
        return skillRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<SkillResponseDTO> findByCategory(String category) {
        return skillRepository.findByCategory(category).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private void applyRequest(Skill skill, SkillRequestDTO requestDTO) {
        skill.setName(requestDTO.getName());
        skill.setCategory(requestDTO.getCategory());
        skill.setDescription(requestDTO.getDescription());
        skill.setStatus(requestDTO.getStatus());
    }

    private SkillResponseDTO toResponseDTO(Skill skill) {
        return new SkillResponseDTO(
                skill.getSkillId(),
                skill.getName(),
                skill.getCategory(),
                skill.getDescription(),
                skill.getStatus(),
                skill.getCreatedAt(),
                skill.getUpdatedAt());
    }
}
