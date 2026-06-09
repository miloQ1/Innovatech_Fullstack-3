package com.example.notificaciones.service;

import com.example.notificaciones.model.WebhookSubscription;
import com.example.notificaciones.repository.WebhookSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebhookSubscriptionService {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;

    public WebhookSubscriptionService(WebhookSubscriptionRepository webhookSubscriptionRepository) {
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
    }

    public WebhookSubscription createSubscription(WebhookSubscription webhookSubscription) {
        if (webhookSubscription.getIsActive() == null) {
            webhookSubscription.setIsActive(true);
        }
        return webhookSubscriptionRepository.save(webhookSubscription);
    }

    public List<WebhookSubscription> getAllSubscriptions() {
        return webhookSubscriptionRepository.findAll();
    }

    public WebhookSubscription getSubscriptionById(Long id) {
        return webhookSubscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada con id: " + id));
    }

    public WebhookSubscription updateSubscription(Long id, WebhookSubscription webhookSubscription) {
        WebhookSubscription existente = getSubscriptionById(id);
        existente.setTargetSystem(webhookSubscription.getTargetSystem());
        existente.setEventType(webhookSubscription.getEventType());
        existente.setEndpointUrl(webhookSubscription.getEndpointUrl());
        existente.setSecretKey(webhookSubscription.getSecretKey());
        existente.setIsActive(webhookSubscription.getIsActive());
        return webhookSubscriptionRepository.save(existente);
    }

    public void deleteSubscription(Long id) {
        WebhookSubscription existente = getSubscriptionById(id);
        existente.setIsActive(false);
        webhookSubscriptionRepository.save(existente);
    }
}
