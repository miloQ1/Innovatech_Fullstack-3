package com.example.ms_recursos_innovatech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Professional professional;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_role")
    private String projectRole;

    @Column(name = "allocation_pct")
    private Integer allocationPct;

    @Column(name = "planned_hours")
    private Integer plannedHours;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "assignment_status")
    private String assignmentStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Assignment() {
    }

    public Assignment(Long assignmentId, Professional professional, Long projectId, String projectRole,
            Integer allocationPct, Integer plannedHours, LocalDate startDate, LocalDate endDate,
            String assignmentStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.assignmentId = assignmentId;
        this.professional = professional;
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

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
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
