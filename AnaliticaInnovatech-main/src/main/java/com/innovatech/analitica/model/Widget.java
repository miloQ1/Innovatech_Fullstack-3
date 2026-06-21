package com.innovatech.analitica.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.innovatech.analitica.model.enums.WidgetType;

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
@Table(name = "widgets")
public class Widget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "widget_id")
    private Long widgetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "widget_type", nullable = false, length = 30)
    private WidgetType widgetType;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "source_kpi_code", length = 50)
    private String sourceKpiCode;

    @Column(name = "configuration_json", columnDefinition = "TEXT")
    private String configurationJson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "widget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LayoutItem> layoutItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.isActive == null) { this.isActive = true; }
    }

    public Long getWidgetId() { return widgetId; }
    public void setWidgetId(Long widgetId) { this.widgetId = widgetId; }
    public WidgetType getWidgetType() { return widgetType; }
    public void setWidgetType(WidgetType widgetType) { this.widgetType = widgetType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSourceKpiCode() { return sourceKpiCode; }
    public void setSourceKpiCode(String sourceKpiCode) { this.sourceKpiCode = sourceKpiCode; }
    public String getConfigurationJson() { return configurationJson; }
    public void setConfigurationJson(String configurationJson) { this.configurationJson = configurationJson; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public List<LayoutItem> getLayoutItems() { return layoutItems; }
    public void setLayoutItems(List<LayoutItem> layoutItems) { this.layoutItems = layoutItems; }
}
