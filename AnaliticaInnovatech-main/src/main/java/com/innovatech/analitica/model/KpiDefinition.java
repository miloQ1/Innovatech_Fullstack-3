package com.innovatech.analitica.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.innovatech.analitica.model.enums.FormulaType;
import com.innovatech.analitica.model.enums.RefreshFrequency;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "kpi_definitions")
public class KpiDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kpi_id")
    private Long kpiId;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "formula_type", nullable = false, length = 30)
    private FormulaType formulaType;

    @Column(length = 30)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "refresh_frequency", nullable = false, length = 20)
    private RefreshFrequency refreshFrequency;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "kpiDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KpiSnapshot> snapshots = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "kpiDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlertRule> alertRules = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    public Long getKpiId() { return kpiId; }
    public void setKpiId(Long kpiId) { this.kpiId = kpiId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public FormulaType getFormulaType() { return formulaType; }
    public void setFormulaType(FormulaType formulaType) { this.formulaType = formulaType; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public RefreshFrequency getRefreshFrequency() { return refreshFrequency; }
    public void setRefreshFrequency(RefreshFrequency refreshFrequency) { this.refreshFrequency = refreshFrequency; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public List<KpiSnapshot> getSnapshots() { return snapshots; }
    public void setSnapshots(List<KpiSnapshot> snapshots) { this.snapshots = snapshots; }
    public List<AlertRule> getAlertRules() { return alertRules; }
    public void setAlertRules(List<AlertRule> alertRules) { this.alertRules = alertRules; }
}
