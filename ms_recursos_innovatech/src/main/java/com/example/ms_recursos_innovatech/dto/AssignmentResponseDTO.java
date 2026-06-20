package com.example.ms_recursos_innovatech.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentResponseDTO {

    private Long assignmentId;
    private Long resourceId;
    private Long projectId;
    private String projectRole;
    private Integer allocationPct;
    private Integer plannedHours;
    private LocalDate startDate;
    private LocalDate endDate;
    private String assignmentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssignmentResponseDTO() {
    }

    public AssignmentResponseDTO(Long assignmentId, Long resourceId, Long projectId, String projectRole,
            Integer allocationPct, Integer plannedHours, LocalDate startDate, LocalDate endDate,
            String assignmentStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.assignmentId = assignmentId;
        this.resourceId = resourceId;
        this.projectId = projectId;
        this.projectRole = projectRole;
        this.allocationPct = allocationPct;
        this.plannedHours = plannedHours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assignmentStatus = assignmentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }

    public Integer getAllocationPct() {
        return allocationPct;
    }

    public void setAllocationPct(Integer allocationPct) {
        this.allocationPct = allocationPct;
    }

    public Integer getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(Integer plannedHours) {
        this.plannedHours = plannedHours;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
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
