package cl.innovatech.servicio_proyectos.service;

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

import cl.innovatech.servicio_proyectos.util.UserContext;

// Notifica a los recursos asignados a un proyecto cuando ocurre un evento relevante
// (tarea creada, cambio de estado, etc.), excluyendo a quien generó la acción.
@Service
public class NotificationPublisherService {

    private final RestTemplate restTemplate;

    @Value("${notificaciones.base-url:http://localhost:8085}")
    private String notificacionesBaseUrl;

    @Value("${ms-recursos.base-url:http://localhost:8083}")
    private String recursosBaseUrl;

    @Value("${ms-asignaciones.base-url:http://localhost:8091}")
    private String asignacionesBaseUrl;

    public NotificationPublisherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyProject(Long projectId, String eventType, Map<String, Object> payload) {
        try {
            Long actorResourceId = resolveActorResourceId();

            List<Long> recipientIds = fetchProjectResourceIds(projectId);
            if (actorResourceId != null) {
                recipientIds = recipientIds.stream()
                        .filter(id -> !id.equals(actorResourceId))
                        .collect(Collectors.toList());
            }
            if (recipientIds.isEmpty()) {
                return;
            }

            Map<String, Object> request = new HashMap<>();
            request.put("sourceService", "servicio-proyectos");
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

    private Long resolveActorResourceId() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId);
            Map<?, ?> professional = restTemplate.exchange(
                recursosBaseUrl + "/api/professionals/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
            ).getBody();
            if (professional == null || professional.get("resourceId") == null) {
                return null;
            }
            return ((Number) professional.get("resourceId")).longValue();
        } catch (Exception e) {
            return null;
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
