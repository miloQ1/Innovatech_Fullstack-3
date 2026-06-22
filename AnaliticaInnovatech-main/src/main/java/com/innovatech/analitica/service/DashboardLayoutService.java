package com.innovatech.analitica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.innovatech.analitica.model.DashboardLayout;
import com.innovatech.analitica.repository.DashboardLayoutRepository;

@Service
public class DashboardLayoutService {

    private final DashboardLayoutRepository dashboardLayoutRepository;

    public DashboardLayoutService(DashboardLayoutRepository dashboardLayoutRepository) {
        this.dashboardLayoutRepository = dashboardLayoutRepository;
    }

    public DashboardLayout createLayout(DashboardLayout dashboardLayout) {
        if (dashboardLayout.getIsDefault() == null) {
            dashboardLayout.setIsDefault(false);
        }
        return dashboardLayoutRepository.save(dashboardLayout);
    }

    public List<DashboardLayout> getAllLayouts() { return dashboardLayoutRepository.findAll(); }

    public DashboardLayout getLayoutById(Long id) {
        return dashboardLayoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Layout no encontrado con id: " + id));
    }

    public DashboardLayout updateLayout(Long id, DashboardLayout dashboardLayout) {
        DashboardLayout existente = getLayoutById(id);
        existente.setOwnerRole(dashboardLayout.getOwnerRole());
        existente.setName(dashboardLayout.getName());
        existente.setIsDefault(dashboardLayout.getIsDefault());
        return dashboardLayoutRepository.save(existente);
    }

    public void deleteLayout(Long id) {
        DashboardLayout existente = getLayoutById(id);
        dashboardLayoutRepository.delete(existente);
    }
}
