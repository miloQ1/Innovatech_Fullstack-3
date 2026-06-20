package com.example.ms_recursos_innovatech.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AbsenceResponseDTO {

    private Long absenceId;
    private Long resourceId;
    private String absenceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer hoursAffected;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AbsenceResponseDTO() {
    }

    public AbsenceResponseDTO(Long absenceId, Long resourceId, String absenceType, LocalDate startDate,
            LocalDate endDate, Integer hoursAffected, String reason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.absenceId = absenceId;
        this.resourceId = resourceId;
        this.absenceType = absenceType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursAffected = hoursAffected;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getAbsenceId() {
        return absenceId;
    }

    public void setAbsenceId(Long absenceId) {
        this.absenceId = absenceId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
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

    public Integer getHoursAffected() {
        return hoursAffected;
    }

    public void setHoursAffected(Integer hoursAffected) {
        this.hoursAffected = hoursAffected;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
