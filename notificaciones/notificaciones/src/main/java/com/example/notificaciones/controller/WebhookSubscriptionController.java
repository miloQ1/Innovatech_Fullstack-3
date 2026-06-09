package com.example.notificaciones.controller;

import com.example.notificaciones.model.WebhookSubscription;
import com.example.notificaciones.service.WebhookSubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookSubscriptionController {

    private final WebhookSubscriptionService webhookSubscriptionService;

    public WebhookSubscriptionController(WebhookSubscriptionService webhookSubscriptionService) {
        this.webhookSubscriptionService = webhookSubscriptionService;
    }

    @PostMapping
    public ResponseEntity<WebhookSubscription> createSubscription(@RequestBody WebhookSubscription webhookSubscription) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(webhookSubscriptionService.createSubscription(webhookSubscription));
    }

    @GetMapping
    public ResponseEntity<List<WebhookSubscription>> getAllSubscriptions() {
        return ResponseEntity.ok(webhookSubscriptionService.getAllSubscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebhookSubscription> getSubscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(webhookSubscriptionService.getSubscriptionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebhookSubscription> updateSubscription(@PathVariable Long id,
                                                                  @RequestBody WebhookSubscription webhookSubscription) {
        return ResponseEntity.ok(webhookSubscriptionService.updateSubscription(id, webhookSubscription));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        webhookSubscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
