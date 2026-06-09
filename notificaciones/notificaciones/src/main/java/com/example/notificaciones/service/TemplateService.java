package com.example.notificaciones.service;

import com.example.notificaciones.model.Template;
import com.example.notificaciones.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public Template createTemplate(Template template) {
        if (template.getIsActive() == null) {
            template.setIsActive(true);
        }
        if (template.getLanguage() == null) {
            template.setLanguage("es");
        }
        return templateRepository.save(template);
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    public Template getTemplateById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template no encontrado con id: " + id));
    }

    public Template updateTemplate(Long id, Template template) {
        Template existente = getTemplateById(id);
        existente.setEventType(template.getEventType());
        existente.setChannel(template.getChannel());
        existente.setSubjectTemplate(template.getSubjectTemplate());
        existente.setBodyTemplate(template.getBodyTemplate());
        existente.setLanguage(template.getLanguage());
        existente.setIsActive(template.getIsActive());
        return templateRepository.save(existente);
    }

    public void deleteTemplate(Long id) {
        Template existente = getTemplateById(id);
        existente.setIsActive(false);
        templateRepository.save(existente);
    }
}
