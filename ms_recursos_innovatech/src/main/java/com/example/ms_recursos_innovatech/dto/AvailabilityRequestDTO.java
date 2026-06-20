package com.example.ms_recursos_innovatech.dto;

import java.time.LocalDate;

public class AvailabilityRequestDTO {

    private Long resourceId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer availableHours;
    private String availabilityType;
    private String notes;

    public AvailabilityRequestDTO() {
    }

    public AvailabilityRequestDTO(Long resourceId, LocalDate dateFrom, LocalDate dateTo, Integer availableHours,
            String availabilityType, String notes) {
        this.resourceId = resourceId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.availableHours = availableHours;
        this.availabilityType = availabilityType;
        this.notes = notes;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public Integer getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(Integer availableHours) {
        this.availableHours = availableHours;
    }

    public String getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
