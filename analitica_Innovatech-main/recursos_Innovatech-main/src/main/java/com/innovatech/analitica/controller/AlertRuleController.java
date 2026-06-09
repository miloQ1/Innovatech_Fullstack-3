package com.innovatech.analitica.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innovatech.analitica.model.AlertRule;
import com.innovatech.analitica.service.AlertRuleService;

@RestController
@RequestMapping("/api/alerts")
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    public AlertRuleController(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @PostMapping("/kpi/{kpiId}")
    public ResponseEntity<AlertRule> createRule(@PathVariable Long kpiId,
                                                @RequestBody AlertRule alertRule) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertRuleService.createRule(kpiId, alertRule));
    }

    @GetMapping
    public ResponseEntity<List<AlertRule>> getAllRules() {
        return ResponseEntity.ok(alertRuleService.getAllRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertRule> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(alertRuleService.getRuleById(id));
    }

    @GetMapping("/kpi/{kpiId}")
    public ResponseEntity<List<AlertRule>> getRulesByKpi(@PathVariable Long kpiId) {
        return ResponseEntity.ok(alertRuleService.getRulesByKpi(kpiId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertRule> updateRule(@PathVariable Long id,
                                                @RequestBody AlertRule alertRule) {
        return ResponseEntity.ok(alertRuleService.updateRule(id, alertRule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        alertRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
