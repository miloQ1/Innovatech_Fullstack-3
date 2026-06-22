package com.example.ms_asignaciones.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Notifica al recurso afectado cuando se le asigna o desasigna de un proyecto.
@Service
public class NotificationPublisherService {

    private final RestTemplate restTemplate;

    @Value("${notificaciones.base-url:http://localhost:8085}")
    private String notificacionesBaseUrl;

    public NotificationPublisherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyResource(Long resourceId, Long projectId, String eventType, Map<String, Object> payload) {
        if (resourceId == null) {
            return;
        }
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("sourceService", "ms-asignaciones");
            request.put("eventType", eventType);
            request.put("entityId", projectId);
            request.put("recipientResourceIds", List.of(resourceId));
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
}
