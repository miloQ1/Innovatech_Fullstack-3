package com.innovatech.analitica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.innovatech.analitica.model.KpiDefinition;
import com.innovatech.analitica.model.KpiSnapshot;
import com.innovatech.analitica.repository.KpiDefinitionRepository;
import com.innovatech.analitica.repository.KpiSnapshotRepository;

@Service
public class KpiSnapshotService {

    private final KpiSnapshotRepository kpiSnapshotRepository;
    private final KpiDefinitionRepository kpiDefinitionRepository;

    public KpiSnapshotService(KpiSnapshotRepository kpiSnapshotRepository,KpiDefinitionRepository kpiDefinitionRepository) {
        this.kpiSnapshotRepository = kpiSnapshotRepository;
        this.kpiDefinitionRepository = kpiDefinitionRepository;
    }

    public KpiSnapshot createSnapshot(Long kpiId, KpiSnapshot kpiSnapshot) {
        KpiDefinition kpiDefinition = kpiDefinitionRepository.findById(kpiId)
                .orElseThrow(() -> new RuntimeException("KPI no encontrado con id: " + kpiId));
        kpiSnapshot.setKpiDefinition(kpiDefinition);
        return kpiSnapshotRepository.save(kpiSnapshot);
    }

    public List<KpiSnapshot> getAllSnapshots() { return kpiSnapshotRepository.findAll(); }

    public KpiSnapshot getSnapshotById(Long id) {
        return kpiSnapshotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Snapshot no encontrado con id: " + id));
    }

    public List<KpiSnapshot> getSnapshotsByKpi(Long kpiId) {
        return kpiSnapshotRepository.findByKpiDefinitionKpiId(kpiId);
    }

    public KpiSnapshot updateSnapshot(Long id, KpiSnapshot kpiSnapshot) {
        KpiSnapshot existente = getSnapshotById(id);
        existente.setScopeType(kpiSnapshot.getScopeType());
        existente.setScopeId(kpiSnapshot.getScopeId());
        existente.setPeriodStart(kpiSnapshot.getPeriodStart());
        existente.setPeriodEnd(kpiSnapshot.getPeriodEnd());
        existente.setNumericValue(kpiSnapshot.getNumericValue());
        existente.setTextValue(kpiSnapshot.getTextValue());
        existente.setSourceTraceJson(kpiSnapshot.getSourceTraceJson());
        return kpiSnapshotRepository.save(existente);
    }

    public void deleteSnapshot(Long id) {
        KpiSnapshot existente = getSnapshotById(id);
        kpiSnapshotRepository.delete(existente);
    }
}
