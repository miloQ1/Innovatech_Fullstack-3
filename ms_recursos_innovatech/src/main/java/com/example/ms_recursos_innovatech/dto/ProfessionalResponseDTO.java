package com.example.ms_recursos_innovatech.dto;

import java.time.LocalDateTime;

public class ProfessionalResponseDTO {

    private Long resourceId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    private String seniority;
    private String location;
    private String timeZone;
    private Integer weeklyCapacityHours;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProfessionalResponseDTO() {
    }

    public ProfessionalResponseDTO(Long resourceId, String employeeCode, String firstName, String lastName,
            String email, String roleName, String seniority, String location, String timeZone,
            Integer weeklyCapacityHours, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.resourceId = resourceId;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleName = roleName;
        this.seniority = seniority;
        this.location = location;
        this.timeZone = timeZone;
        this.weeklyCapacityHours = weeklyCapacityHours;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getSeniority() {
        return seniority;
    }

    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Integer getWeeklyCapacityHours() {
        return weeklyCapacityHours;
    }

    public void setWeeklyCapacityHours(Integer weeklyCapacityHours) {
        this.weeklyCapacityHours = weeklyCapacityHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
