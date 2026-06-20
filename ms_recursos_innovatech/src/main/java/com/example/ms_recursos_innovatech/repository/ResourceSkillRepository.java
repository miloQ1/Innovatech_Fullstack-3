package com.example.ms_recursos_innovatech.repository;

import com.example.ms_recursos_innovatech.model.ResourceSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceSkillRepository extends JpaRepository<ResourceSkill, Long> {

    List<ResourceSkill> findByProfessionalResourceId(Long resourceId);

    List<ResourceSkill> findBySkillSkillId(Long skillId);
}
