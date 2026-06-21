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

import com.innovatech.analitica.model.KpiDefinition;
import com.innovatech.analitica.service.KpiDefinitionService;

@RestController
@RequestMapping("/api/kpis")
public class KpiDefinitionController {

    private final KpiDefinitionService kpiDefinitionService;

    public KpiDefinitionController(KpiDefinitionService kpiDefinitionService) {
        this.kpiDefinitionService = kpiDefinitionService;
    }

    @PostMapping
    public ResponseEntity<KpiDefinition> createKpi(@RequestBody KpiDefinition kpiDefinition) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kpiDefinitionService.createKpi(kpiDefinition));
    }

    @GetMapping
    public ResponseEntity<List<KpiDefinition>> getAllKpis() {
        return ResponseEntity.ok(kpiDefinitionService.getAllKpis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KpiDefinition> getKpiById(@PathVariable Long id) {
        return ResponseEntity.ok(kpiDefinitionService.getKpiById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KpiDefinition> updateKpi(@PathVariable Long id, @RequestBody KpiDefinition kpiDefinition) {
        return ResponseEntity.ok(kpiDefinitionService.updateKpi(id, kpiDefinition));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKpi(@PathVariable Long id) {
        kpiDefinitionService.deleteKpi(id);
        return ResponseEntity.noContent().build();
    }
}
