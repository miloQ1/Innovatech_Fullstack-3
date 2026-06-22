package com.example.notificaciones.controller;

import com.example.notificaciones.dto.DispatchResultDTO;
import com.example.notificaciones.dto.NotificationRequestDTO;
import com.example.notificaciones.dto.NotificationResponseDTO;
import com.example.notificaciones.service.NotificationDispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Procesamiento y consulta de notificaciones")
public class NotificationController {

    private final NotificationDispatchService notificationDispatchService;

    public NotificationController(NotificationDispatchService notificationDispatchService) {
        this.notificationDispatchService = notificationDispatchService;
    }

    @PostMapping("/send")
    @Operation(summary = "Procesar una notificación por uno o más canales (solo ADMIN)")
    public ResponseEntity<NotificationResponseDTO> send(@RequestBody NotificationRequestDTO request,
                                                         @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere rol ADMIN para enviar notificaciones de prueba");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationDispatchService.send(request));
    }

    @GetMapping("/recipient/{recipientResourceId}")
    @Operation(summary = "Listar notificaciones de un recurso o usuario")
    public ResponseEntity<List<DispatchResultDTO>> findByRecipient(@PathVariable Long recipientResourceId) {
        return ResponseEntity.ok(notificationDispatchService.findInboxByRecipient(recipientResourceId));
    }
}
