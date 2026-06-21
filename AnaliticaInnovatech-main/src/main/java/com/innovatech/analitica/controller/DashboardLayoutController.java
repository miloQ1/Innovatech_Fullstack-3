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

import com.innovatech.analitica.model.DashboardLayout;
import com.innovatech.analitica.service.DashboardLayoutService;

@RestController
@RequestMapping("/api/layouts")
public class DashboardLayoutController {

    private final DashboardLayoutService dashboardLayoutService;

    public DashboardLayoutController(DashboardLayoutService dashboardLayoutService) {
        this.dashboardLayoutService = dashboardLayoutService;
    }

    @PostMapping
    public ResponseEntity<DashboardLayout> createLayout(@RequestBody DashboardLayout dashboardLayout) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dashboardLayoutService.createLayout(dashboardLayout));
    }

    @GetMapping
    public ResponseEntity<List<DashboardLayout>> getAllLayouts() {
        return ResponseEntity.ok(dashboardLayoutService.getAllLayouts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardLayout> getLayoutById(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardLayoutService.getLayoutById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DashboardLayout> updateLayout(@PathVariable Long id,
                                                        @RequestBody DashboardLayout dashboardLayout) {
        return ResponseEntity.ok(dashboardLayoutService.updateLayout(id, dashboardLayout));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLayout(@PathVariable Long id) {
        dashboardLayoutService.deleteLayout(id);
        return ResponseEntity.noContent().build();
    }
}
