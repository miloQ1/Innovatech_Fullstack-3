package com.example.notificaciones.repository;

import com.example.notificaciones.model.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {
    List<WebhookSubscription> findByIsActiveTrue();
    List<WebhookSubscription> findByEventTypeAndIsActiveTrue(String eventType);
}
