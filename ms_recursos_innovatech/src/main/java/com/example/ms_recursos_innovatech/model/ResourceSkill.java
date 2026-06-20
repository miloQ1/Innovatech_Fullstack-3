package com.example.ms_recursos_innovatech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_skills")
public class ResourceSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_skill_id")
    private Long resourceSkillId;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Professional professional;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "proficiency_level")
    private String proficiencyLevel;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "certified")
    private Boolean certified;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public ResourceSkill() {
    }

    public ResourceSkill(Long resourceSkillId, Professional professional, Skill skill, String proficiencyLevel,
            Integer yearsExperience, Boolean certified, LocalDateTime lastUpdated) {
        this.resourceSkillId = resourceSkillId;
        this.professional = professional;
        this.skill = skill;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsExperience = yearsExperience;
        this.certified = certified;
        this.lastUpdated = lastUpdated;
    }

    public Long getResourceSkillId() {
        return resourceSkillId;
    }

    public void setResourceSkillId(Long resourceSkillId) {
        this.resourceSkillId = resourceSkillId;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public Boolean getCertified() {
        return certified;
    }

    public void setCertified(Boolean certified) {
        this.certified = certified;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
