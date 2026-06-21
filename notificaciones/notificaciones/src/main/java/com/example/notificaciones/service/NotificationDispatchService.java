package com.example.notificaciones.service;

import com.example.notificaciones.dto.DispatchResultDTO;
import com.example.notificaciones.dto.NotificationRequestDTO;
import com.example.notificaciones.dto.NotificationResponseDTO;
import com.example.notificaciones.model.Dispatch;
import com.example.notificaciones.model.NotificationEvent;
import com.example.notificaciones.model.Preference;
import com.example.notificaciones.model.Template;
import com.example.notificaciones.model.enums.DeliveryStatus;
import com.example.notificaciones.model.enums.EventStatus;
import com.example.notificaciones.model.enums.NotificationChannel;
import com.example.notificaciones.notifier.Notifier;
import com.example.notificaciones.notifier.NotifierFactory;
import com.example.notificaciones.repository.DispatchRepository;
import com.example.notificaciones.repository.NotificationEventRepository;
import com.example.notificaciones.repository.PreferenceRepository;
import com.example.notificaciones.repository.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationDispatchService {

    private final TemplateRepository templateRepository;
    private final NotificationEventRepository eventRepository;
    private final DispatchRepository dispatchRepository;
    private final PreferenceRepository preferenceRepository;
    private final TemplateRendererService templateRendererService;
    private final NotifierFactory notifierFactory;
    private final ObjectMapper objectMapper;

    public NotificationDispatchService(TemplateRepository templateRepository,
                                       NotificationEventRepository eventRepository,
                                       DispatchRepository dispatchRepository,
                                       PreferenceRepository preferenceRepository,
                                       TemplateRendererService templateRendererService,
                                       NotifierFactory notifierFactory,
                                       ObjectMapper objectMapper) {
        this.templateRepository = templateRepository;
        this.eventRepository = eventRepository;
        this.dispatchRepository = dispatchRepository;
        this.preferenceRepository = preferenceRepository;
        this.templateRendererService = templateRendererService;
        this.notifierFactory = notifierFactory;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public NotificationResponseDTO send(NotificationRequestDTO request) {
        validateRequest(request);

        Map<String, Object> payload = buildPayload(request);
        List<NotificationChannel> channels = resolveChannels(request.getChannels());
        List<DispatchResultDTO> results = new ArrayList<>();

        for (NotificationChannel channel : channels) {
            Template template = resolveTemplate(request.getEventType(), channel);
            NotificationEvent event = createEvent(request, template, payload);

            boolean hasFailure = false;
            boolean hasSent = false;

            for (Long recipientResourceId : request.getRecipientResourceIds()) {
                Dispatch dispatch = createPendingDispatch(event, template, recipientResourceId, channel, payload);

                try {
                    if (!isChannelEnabledForRecipient(recipientResourceId, channel)) {
                        dispatch.setDeliveryStatus(DeliveryStatus.SKIPPED);
                        dispatch.setErrorMessage("El destinatario tiene desactivado el canal " + channel);
                    } else {
                        Notifier notifier = notifierFactory.getNotifier(channel);
                        dispatch = notifier.send(dispatch);
                        hasSent = true;
                    }
                } catch (Exception ex) {
                    dispatch.setDeliveryStatus(DeliveryStatus.FAILED);
                    dispatch.setErrorMessage(ex.getMessage());
                    hasFailure = true;
                }

                dispatch = dispatchRepository.save(dispatch);
                results.add(toDispatchResultDTO(dispatch));
            }

            if (hasFailure && !hasSent) {
                event.setEventStatus(EventStatus.FAILED);
            } else {
                event.setEventStatus(EventStatus.PROCESSED);
            }
            eventRepository.save(event);
        }

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setMessage("Notificación procesada correctamente");
        response.setTotalDispatches(results.size());
        response.setDispatches(results);
        return response;
    }

    @Transactional(readOnly = true)
    public List<DispatchResultDTO> findInboxByRecipient(Long recipientResourceId) {
        return dispatchRepository.findByRecipientResourceIdOrderBySentAtDesc(recipientResourceId)
                .stream()
                .map(this::toDispatchResultDTO)
                .toList();
    }

    private void validateRequest(NotificationRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio");
        }
        if (request.getSourceService() == null || request.getSourceService().isBlank()) {
            throw new IllegalArgumentException("sourceService es obligatorio");
        }
        if (request.getEventType() == null || request.getEventType().isBlank()) {
            throw new IllegalArgumentException("eventType es obligatorio");
        }
        if (request.getRecipientResourceIds() == null || request.getRecipientResourceIds().isEmpty()) {
            throw new IllegalArgumentException("Debe informar al menos un recipientResourceId");
        }
    }

    private List<NotificationChannel> resolveChannels(List<NotificationChannel> channels) {
        if (channels == null || channels.isEmpty()) {
            return List.of(NotificationChannel.IN_APP);
        }
        return channels;
    }

    private Map<String, Object> buildPayload(NotificationRequestDTO request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (request.getPayload() != null) {
            payload.putAll(request.getPayload());
        }
        payload.putIfAbsent("sourceService", request.getSourceService());
        payload.putIfAbsent("eventType", request.getEventType());
        payload.putIfAbsent("entityId", request.getEntityId() == null ? "" : request.getEntityId());
        return payload;
    }

    private Template resolveTemplate(String eventType, NotificationChannel channel) {
        return templateRepository.findByEventTypeAndChannelAndIsActiveTrue(eventType, channel)
                .orElseGet(() -> templateRepository.save(createDefaultTemplate(eventType, channel)));
    }

    private Template createDefaultTemplate(String eventType, NotificationChannel channel) {
        Template template = new Template();
        template.setEventType(eventType);
        template.setChannel(channel);
        template.setLanguage("es");
        template.setIsActive(true);
        template.setSubjectTemplate("Innovatech - {{eventType}}");
        template.setBodyTemplate("Se generó el evento {{eventType}} desde {{sourceService}} para la entidad {{entityId}}.");
        return template;
    }

    private NotificationEvent createEvent(NotificationRequestDTO request, Template template, Map<String, Object> payload) {
        NotificationEvent event = new NotificationEvent();
        event.setTemplate(template);
        event.setSourceService(request.getSourceService());
        event.setEventType(request.getEventType());
        event.setEntityId(request.getEntityId());
        event.setPayloadJson(toJson(payload));
        event.setEventStatus(EventStatus.PENDING);
        return eventRepository.save(event);
    }

    private Dispatch createPendingDispatch(NotificationEvent event,
                                           Template template,
                                           Long recipientResourceId,
                                           NotificationChannel channel,
                                           Map<String, Object> payload) {
        Dispatch dispatch = new Dispatch();
        dispatch.setNotificationEvent(event);
        dispatch.setRecipientResourceId(recipientResourceId);
        dispatch.setChannel(channel);
        dispatch.setDeliveryStatus(DeliveryStatus.PENDING);
        dispatch.setRetryCount(0);
        dispatch.setRenderedSubject(templateRendererService.render(template.getSubjectTemplate(), payload));
        dispatch.setRenderedBody(templateRendererService.render(template.getBodyTemplate(), payload));
        return dispatch;
    }

    private boolean isChannelEnabledForRecipient(Long recipientResourceId, NotificationChannel channel) {
        Preference preference = preferenceRepository.findByResourceIdAndChannel(recipientResourceId, channel)
                .orElse(null);
        return preference == null || Boolean.TRUE.equals(preference.getEnabled());
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    private DispatchResultDTO toDispatchResultDTO(Dispatch dispatch) {
        DispatchResultDTO dto = new DispatchResultDTO();
        if (dispatch.getNotificationEvent() != null) {
            dto.setEventId(dispatch.getNotificationEvent().getEventId());
        }
        dto.setDispatchId(dispatch.getDispatchId());
        dto.setRecipientResourceId(dispatch.getRecipientResourceId());
        dto.setChannel(dispatch.getChannel());
        dto.setDeliveryStatus(dispatch.getDeliveryStatus());
        dto.setSubject(dispatch.getRenderedSubject());
        dto.setBody(dispatch.getRenderedBody());
        dto.setSentAt(dispatch.getSentAt());
        dto.setErrorMessage(dispatch.getErrorMessage());
        return dto;
    }
}
