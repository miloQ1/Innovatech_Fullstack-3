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

import com.innovatech.analitica.model.KpiSnapshot;
import com.innovatech.analitica.service.KpiSnapshotService;

@RestController
@RequestMapping("/api/snapshots")
public class KpiSnapshotController {

    private final KpiSnapshotService kpiSnapshotService;

    public KpiSnapshotController(KpiSnapshotService kpiSnapshotService) {
        this.kpiSnapshotService = kpiSnapshotService;
    }

    @PostMapping("/kpi/{kpiId}")
    public ResponseEntity<KpiSnapshot> createSnapshot(@PathVariable Long kpiId, @RequestBody KpiSnapshot kpiSnapshot) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(kpiSnapshotService.createSnapshot(kpiId, kpiSnapshot));
    }

    @GetMapping
    public ResponseEntity<List<KpiSnapshot>> getAllSnapshots() {
        return ResponseEntity.ok(kpiSnapshotService.getAllSnapshots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KpiSnapshot> getSnapshotById(@PathVariable Long id) {
        return ResponseEntity.ok(kpiSnapshotService.getSnapshotById(id));
    }

    @GetMapping("/kpi/{kpiId}")
    public ResponseEntity<List<KpiSnapshot>> getSnapshotsByKpi(@PathVariable Long kpiId) {
        return ResponseEntity.ok(kpiSnapshotService.getSnapshotsByKpi(kpiId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KpiSnapshot> updateSnapshot(@PathVariable Long id, @RequestBody KpiSnapshot kpiSnapshot) {
        return ResponseEntity.ok(kpiSnapshotService.updateSnapshot(id, kpiSnapshot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnapshot(@PathVariable Long id) {
        kpiSnapshotService.deleteSnapshot(id);
        return ResponseEntity.noContent().build();
    }
}
