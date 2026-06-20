package com.example.ms_recursos_innovatech.repository;

import com.example.ms_recursos_innovatech.model.Skill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByStatus(String status);

    List<Skill> findByCategory(String category);
}
