package com.example.ms_recursos_innovatech.dto;

import java.time.LocalDateTime;

public class ResourceSkillResponseDTO {

    private Long resourceSkillId;
    private Long resourceId;
    private Long skillId;
    private String skillName;
    private String proficiencyLevel;
    private Integer yearsExperience;
    private Boolean certified;
    private LocalDateTime lastUpdated;

    public ResourceSkillResponseDTO() {
    }

    public ResourceSkillResponseDTO(Long resourceSkillId, Long resourceId, Long skillId, String skillName,
            String proficiencyLevel, Integer yearsExperience, Boolean certified, LocalDateTime lastUpdated) {
        this.resourceSkillId = resourceSkillId;
        this.resourceId = resourceId;
        this.skillId = skillId;
        this.skillName = skillName;
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

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
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
