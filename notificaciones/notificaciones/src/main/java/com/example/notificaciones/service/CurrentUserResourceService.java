package com.example.notificaciones.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CurrentUserResourceService {

    private final RestTemplate restTemplate;

    @Value("${resources.service.url:http://localhost:8083}")
    private String resourcesServiceUrl;

    public CurrentUserResourceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long resolveCurrentResourceId(String userId, String userEmail) {
        if (userId != null && !userId.isBlank()) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-User-Id", userId);
                Map<?, ?> professional = restTemplate.exchange(
                        resourcesServiceUrl + "/api/professionals/me",
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        Map.class
                ).getBody();
                Long id = extractResourceId(professional);
                if (id != null) {
                    return id;
                }
            } catch (ResponseStatusException ex) {
                throw ex;
            } catch (Exception ignored) {
                // Fallback por email para compatibilidad con perfiles antiguos sin employeeCode.
            }
        }

        return resolveCurrentResourceIdByEmail(userEmail);
    }

    public Long resolveCurrentResourceIdByEmail(String userEmail) {
        String email = normalizeEmail(userEmail);
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se recibió el usuario autenticado");
        }

        try {
            Object[] response = restTemplate.getForObject(resourcesServiceUrl + "/api/professionals", Object[].class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe ficha profesional para el usuario logeado");
            }

            for (Object item : response) {
                if (!(item instanceof Map<?, ?> professional)) {
                    continue;
                }
                Object professionalEmail = professional.get("email");
                if (professionalEmail != null && email.equals(normalizeEmail(String.valueOf(professionalEmail)))) {
                    Long id = extractResourceId(professional);
                    if (id != null) {
                        return id;
                    }
                }
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo validar la ficha profesional del usuario logeado");
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No existe ficha profesional asociada al usuario logeado");
    }

    public void assertCurrentUserOwnsResource(String userId, String userEmail, Long requestedResourceId) {
        Long currentResourceId = resolveCurrentResourceId(userId, userEmail);
        if (requestedResourceId == null || !currentResourceId.equals(requestedResourceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo puedes consultar o modificar tus propias notificaciones");
        }
    }

    private static Long extractResourceId(Map<?, ?> professional) {
        if (professional == null) return null;
        Object idValue = professional.get("resourceId");
        if (idValue == null) idValue = professional.get("professionalId");
        if (idValue == null) idValue = professional.get("id");
        return toLong(idValue);
    }

    private static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private static Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value != null) {
            return Long.valueOf(String.valueOf(value));
        }
        return null;
    }
}
