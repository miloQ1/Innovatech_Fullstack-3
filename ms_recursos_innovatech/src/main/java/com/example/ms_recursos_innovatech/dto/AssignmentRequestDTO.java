package com.example.ms_recursos_innovatech.dto;

import java.time.LocalDate;

public class AssignmentRequestDTO {

    private Long resourceId;
    private Long projectId;
    private String projectRole;
    private Integer allocationPct;
    private Integer plannedHours;
    private LocalDate startDate;
    private LocalDate endDate;
    private String assignmentStatus;

    public AssignmentRequestDTO() {
    }

    public AssignmentRequestDTO(Long resourceId, Long projectId, String projectRole, Integer allocationPct,
            Integer plannedHours, LocalDate startDate, LocalDate endDate, String assignmentStatus) {
        this.resourceId = resourceId;
        this.projectId = projectId;
        this.projectRole = projectRole;
        this.allocationPct = allocationPct;
        this.plannedHours = plannedHours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assignmentStatus = assignmentStatus;
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
}
