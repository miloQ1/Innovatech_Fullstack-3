package com.innovatech.analitica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.innovatech.analitica.model.KpiDefinition;
import com.innovatech.analitica.repository.KpiDefinitionRepository;

@Service
public class KpiDefinitionService {

    private final KpiDefinitionRepository kpiDefinitionRepository;

    public KpiDefinitionService(KpiDefinitionRepository kpiDefinitionRepository) {
        this.kpiDefinitionRepository = kpiDefinitionRepository;
    }

    public KpiDefinition createKpi(KpiDefinition kpiDefinition) {
        if (kpiDefinition.getIsActive() == null) {
            kpiDefinition.setIsActive(true);
        }
        return kpiDefinitionRepository.save(kpiDefinition);
    }

    public List<KpiDefinition> getAllKpis() {
        return kpiDefinitionRepository.findAll();
    }

    public KpiDefinition getKpiById(Long id) {
        return kpiDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KPI no encontrado con id: " + id));
    }

    public KpiDefinition updateKpi(Long id, KpiDefinition kpiDefinition) {
        KpiDefinition existente = getKpiById(id);
        existente.setCode(kpiDefinition.getCode());
        existente.setName(kpiDefinition.getName());
        existente.setFormulaType(kpiDefinition.getFormulaType());
        existente.setUnit(kpiDefinition.getUnit());
        existente.setRefreshFrequency(kpiDefinition.getRefreshFrequency());
        existente.setIsActive(kpiDefinition.getIsActive());
        return kpiDefinitionRepository.save(existente);
    }

    public void deleteKpi(Long id) {
        KpiDefinition existente = getKpiById(id);
        existente.setIsActive(false);
        kpiDefinitionRepository.save(existente);
    }
}
