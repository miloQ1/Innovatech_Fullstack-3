package com.innovatech.analitica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.innovatech.analitica.model.AlertRule;
import com.innovatech.analitica.model.KpiDefinition;
import com.innovatech.analitica.repository.AlertRuleRepository;
import com.innovatech.analitica.repository.KpiDefinitionRepository;

@Service
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;
    private final KpiDefinitionRepository kpiDefinitionRepository;

    public AlertRuleService(AlertRuleRepository alertRuleRepository,
                            KpiDefinitionRepository kpiDefinitionRepository) {
        this.alertRuleRepository = alertRuleRepository;
        this.kpiDefinitionRepository = kpiDefinitionRepository;
    }

    public AlertRule createRule(Long kpiId, AlertRule alertRule) {
        KpiDefinition kpiDefinition = kpiDefinitionRepository.findById(kpiId)
                .orElseThrow(() -> new RuntimeException("KPI no encontrado con id: " + kpiId));
        alertRule.setKpiDefinition(kpiDefinition);
        if (alertRule.getIsActive() == null) {
            alertRule.setIsActive(true);
        }
        return alertRuleRepository.save(alertRule);
    }

    public List<AlertRule> getAllRules() { return alertRuleRepository.findAll(); }

    public AlertRule getRuleById(Long id) {
        return alertRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla no encontrada con id: " + id));
    }

    public List<AlertRule> getRulesByKpi(Long kpiId) {
        return alertRuleRepository.findByKpiDefinitionKpiId(kpiId);
    }

    public AlertRule updateRule(Long id, AlertRule alertRule) {
        AlertRule existente = getRuleById(id);
        existente.setOperator(alertRule.getOperator());
        existente.setThresholdValue(alertRule.getThresholdValue());
        existente.setSeverity(alertRule.getSeverity());
        existente.setIsActive(alertRule.getIsActive());
        existente.setNotificationChannel(alertRule.getNotificationChannel());
        return alertRuleRepository.save(existente);
    }

    public void deleteRule(Long id) {
        AlertRule existente = getRuleById(id);
        existente.setIsActive(false);
        alertRuleRepository.save(existente);
    }
}
