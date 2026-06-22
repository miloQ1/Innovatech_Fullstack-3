package com.example.ms_colaboracion_innovatech.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Notifica a los recursos asignados a un proyecto cuando ocurre un evento de colaboración
// (hilo nuevo, comentario nuevo), excluyendo al autor de la acción.
@Service
public class NotificationPublisherService {

    private final RestTemplate restTemplate;

    @Value("${notificaciones.base-url:http://localhost:8085}")
    private String notificacionesBaseUrl;

    @Value("${ms-asignaciones.base-url:http://localhost:8091}")
    private String asignacionesBaseUrl;

    public NotificationPublisherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyProject(Long projectId, String eventType, Long excludeResourceId, Map<String, Object> payload) {
        if (projectId == null) {
            return;
        }
        try {
            List<Long> recipientIds = fetchProjectResourceIds(projectId);
            if (excludeResourceId != null) {
                recipientIds = recipientIds.stream()
                        .filter(id -> !id.equals(excludeResourceId))
                        .collect(Collectors.toList());
            }
            if (recipientIds.isEmpty()) {
                return;
            }

            Map<String, Object> request = new HashMap<>();
            request.put("sourceService", "ms-colaboracion");
            request.put("eventType", eventType);
            request.put("entityId", projectId);
            request.put("recipientResourceIds", recipientIds);
            request.put("channels", List.of("IN_APP"));
            request.put("payload", payload);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Role", "ADMIN");
            restTemplate.exchange(
                notificacionesBaseUrl + "/api/notifications/send",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                Object.class
            );
        } catch (Exception e) {
            System.out.println("Warning: no se pudo notificar el evento " + eventType + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Long> fetchProjectResourceIds(Long projectId) {
        List<Map<String, Object>> assignments = restTemplate.exchange(
            asignacionesBaseUrl + "/api/assignments/project/" + projectId,
            HttpMethod.GET,
            HttpEntity.EMPTY,
            List.class
        ).getBody();

        List<Long> ids = new ArrayList<>();
        if (assignments == null) {
            return ids;
        }
        for (Map<String, Object> assignment : assignments) {
            Object resourceId = assignment.get("resourceId");
            if (resourceId != null) {
                ids.add(((Number) resourceId).longValue());
            }
        }
        return ids;
    }
}
