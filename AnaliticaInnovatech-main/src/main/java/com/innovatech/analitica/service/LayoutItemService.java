package com.innovatech.analitica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.innovatech.analitica.model.DashboardLayout;
import com.innovatech.analitica.model.LayoutItem;
import com.innovatech.analitica.model.Widget;
import com.innovatech.analitica.repository.DashboardLayoutRepository;
import com.innovatech.analitica.repository.LayoutItemRepository;
import com.innovatech.analitica.repository.WidgetRepository;

@Service
public class LayoutItemService {

    private final LayoutItemRepository layoutItemRepository;
    private final DashboardLayoutRepository dashboardLayoutRepository;
    private final WidgetRepository widgetRepository;

    public LayoutItemService(LayoutItemRepository layoutItemRepository,DashboardLayoutRepository dashboardLayoutRepository,WidgetRepository widgetRepository) {
        this.layoutItemRepository = layoutItemRepository;
        this.dashboardLayoutRepository = dashboardLayoutRepository;
        this.widgetRepository = widgetRepository;
    }

    public LayoutItem createLayoutItem(Long layoutId, Long widgetId, LayoutItem layoutItem) {
        DashboardLayout dashboardLayout = dashboardLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Layout no encontrado con id: " + layoutId));
        Widget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new RuntimeException("Widget no encontrado con id: " + widgetId));
        layoutItem.setDashboardLayout(dashboardLayout);
        layoutItem.setWidget(widget);
        return layoutItemRepository.save(layoutItem);
    }

    public LayoutItem getLayoutItemById(Long id) {
        return layoutItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Layout item no encontrado con id: " + id));
    }

    public List<LayoutItem> getItemsByLayout(Long layoutId) {
        return layoutItemRepository.findByDashboardLayoutLayoutIdOrderByDisplayOrderAsc(layoutId);
    }

    public LayoutItem updateLayoutItem(Long id, LayoutItem layoutItem) {
        LayoutItem existente = getLayoutItemById(id);
        existente.setPositionX(layoutItem.getPositionX());
        existente.setPositionY(layoutItem.getPositionY());
        existente.setWidth(layoutItem.getWidth());
        existente.setHeight(layoutItem.getHeight());
        existente.setDisplayOrder(layoutItem.getDisplayOrder());
        return layoutItemRepository.save(existente);
    }

    public void deleteLayoutItem(Long id) {
        LayoutItem existente = getLayoutItemById(id);
        layoutItemRepository.delete(existente);
    }
}
