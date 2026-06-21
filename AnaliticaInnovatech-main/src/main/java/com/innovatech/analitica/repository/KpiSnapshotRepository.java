package com.innovatech.analitica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innovatech.analitica.model.KpiSnapshot;

@Repository
public interface KpiSnapshotRepository extends JpaRepository<KpiSnapshot, Long> {
    List<KpiSnapshot> findByKpiDefinitionKpiId(Long kpiId);
}
