package com.example.ms_recursos_innovatech.dto;

public class ResourceSkillRequestDTO {

    private Long resourceId;
    private Long skillId;
    private String proficiencyLevel;
    private Integer yearsExperience;
    private Boolean certified;

    public ResourceSkillRequestDTO() {
    }

    public ResourceSkillRequestDTO(Long resourceId, Long skillId, String proficiencyLevel, Integer yearsExperience,
            Boolean certified) {
        this.resourceId = resourceId;
        this.skillId = skillId;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsExperience = yearsExperience;
        this.certified = certified;
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
}
