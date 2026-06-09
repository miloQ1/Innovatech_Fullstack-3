package com.example.notificaciones.service;

import com.example.notificaciones.model.NotificationEvent;
import com.example.notificaciones.model.Template;
import com.example.notificaciones.repository.NotificationEventRepository;
import com.example.notificaciones.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationEventService {

    private final NotificationEventRepository notificationEventRepository;
    private final TemplateRepository templateRepository;

    public NotificationEventService(NotificationEventRepository notificationEventRepository,
                                    TemplateRepository templateRepository) {
        this.notificationEventRepository = notificationEventRepository;
        this.templateRepository = templateRepository;
    }

    public NotificationEvent createEvent(Long templateId, NotificationEvent notificationEvent) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template no encontrado con id: " + templateId));
        notificationEvent.setTemplate(template);
        return notificationEventRepository.save(notificationEvent);
    }

    public List<NotificationEvent> getAllEvents() {
        return notificationEventRepository.findAll();
    }

    public NotificationEvent getEventById(Long id) {
        return notificationEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + id));
    }

    public List<NotificationEvent> getEventsByTemplate(Long templateId) {
        return notificationEventRepository.findByTemplateTemplateId(templateId);
    }

    public NotificationEvent updateEvent(Long id, NotificationEvent notificationEvent) {
        NotificationEvent existente = getEventById(id);
        existente.setSourceService(notificationEvent.getSourceService());
        existente.setEventType(notificationEvent.getEventType());
        existente.setEntityId(notificationEvent.getEntityId());
        existente.setPayloadJson(notificationEvent.getPayloadJson());
        existente.setEventStatus(notificationEvent.getEventStatus());
        return notificationEventRepository.save(existente);
    }

    public void deleteEvent(Long id) {
        NotificationEvent existente = getEventById(id);
        notificationEventRepository.delete(existente);
    }
}
