package com.example.notificaciones.controller;

import com.example.notificaciones.model.NotificationEvent;
import com.example.notificaciones.service.NotificationEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class NotificationEventController {

    private final NotificationEventService notificationEventService;

    public NotificationEventController(NotificationEventService notificationEventService) {
        this.notificationEventService = notificationEventService;
    }

    @PostMapping("/template/{templateId}")
    public ResponseEntity<NotificationEvent> createEvent(@PathVariable Long templateId,
                                                         @RequestBody NotificationEvent notificationEvent) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationEventService.createEvent(templateId, notificationEvent));
    }

    @GetMapping
    public ResponseEntity<List<NotificationEvent>> getAllEvents() {
        return ResponseEntity.ok(notificationEventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationEvent> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationEventService.getEventById(id));
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<NotificationEvent>> getEventsByTemplate(@PathVariable Long templateId) {
        return ResponseEntity.ok(notificationEventService.getEventsByTemplate(templateId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationEvent> updateEvent(@PathVariable Long id,
                                                         @RequestBody NotificationEvent notificationEvent) {
        return ResponseEntity.ok(notificationEventService.updateEvent(id, notificationEvent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        notificationEventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}