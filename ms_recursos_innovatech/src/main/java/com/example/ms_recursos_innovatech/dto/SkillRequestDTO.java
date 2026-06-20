package com.example.ms_recursos_innovatech.dto;

public class SkillRequestDTO {

    private String name;
    private String category;
    private String description;
    private String status;

    public SkillRequestDTO() {
    }

    public SkillRequestDTO(String name, String category, String description, String status) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
