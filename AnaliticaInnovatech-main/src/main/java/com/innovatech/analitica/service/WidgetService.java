package com.innovatech.analitica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.innovatech.analitica.model.Widget;
import com.innovatech.analitica.repository.WidgetRepository;

@Service
public class WidgetService {

    private final WidgetRepository widgetRepository;

    public WidgetService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public Widget createWidget(Widget widget) {
        if (widget.getIsActive() == null) {
            widget.setIsActive(true);
        }
        return widgetRepository.save(widget);
    }

    public List<Widget> getAllWidgets() { return widgetRepository.findAll(); }

    public Widget getWidgetById(Long id) {
        return widgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Widget no encontrado con id: " + id));
    }

    public Widget updateWidget(Long id, Widget widget) {
        Widget existente = getWidgetById(id);
        existente.setWidgetType(widget.getWidgetType());
        existente.setTitle(widget.getTitle());
        existente.setSourceKpiCode(widget.getSourceKpiCode());
        existente.setConfigurationJson(widget.getConfigurationJson());
        existente.setIsActive(widget.getIsActive());
        return widgetRepository.save(existente);
    }

    public void deleteWidget(Long id) {
        Widget existente = getWidgetById(id);
        existente.setIsActive(false);
        widgetRepository.save(existente);
    }
}
