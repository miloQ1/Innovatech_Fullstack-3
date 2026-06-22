package com.example.notificaciones.controller;

import com.example.notificaciones.dto.DispatchResultDTO;
import com.example.notificaciones.dto.NotificationRequestDTO;
import com.example.notificaciones.dto.NotificationResponseDTO;
import com.example.notificaciones.service.CurrentUserResourceService;
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
    private final CurrentUserResourceService currentUserResourceService;

    public NotificationController(NotificationDispatchService notificationDispatchService,
                                  CurrentUserResourceService currentUserResourceService) {
        this.notificationDispatchService = notificationDispatchService;
        this.currentUserResourceService = currentUserResourceService;
    }

    @PostMapping("/send")
    @Operation(summary = "Procesar una notificación por uno o más canales (ADMIN o servicio interno)")
    public ResponseEntity<NotificationResponseDTO> send(@RequestBody NotificationRequestDTO request,
                                                         @RequestHeader(value = "X-User-Role", required = false) String role,
                                                         @RequestHeader(value = "X-Internal-Service", required = false) String internalService) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean isInternal = internalService != null && !internalService.isBlank();
        if (!isAdmin && !isInternal) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere rol ADMIN o servicio interno para enviar notificaciones");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationDispatchService.send(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Mi bandeja de notificaciones", description = "Solo devuelve notificaciones del usuario autenticado")
    public ResponseEntity<List<DispatchResultDTO>> findMyInbox(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                               @RequestHeader(value = "X-User-Email", required = false) String email,
                                                               @RequestHeader(value = "X-User-First-Name", required = false) String firstName,
                                                               @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long resourceId = currentUserResourceService.resolveCurrentResourceId(userId, email);
        notificationDispatchService.ensureWelcomeNotification(resourceId, firstName, userName);
        return ResponseEntity.ok(notificationDispatchService.findInboxByRecipient(resourceId));
    }

    @PostMapping("/test-me")
    @Operation(summary = "Enviar una notificación de prueba a mi bandeja")
    public ResponseEntity<NotificationResponseDTO> sendTestToMe(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                                 @RequestHeader(value = "X-User-Email", required = false) String email,
                                                                 @RequestHeader(value = "X-User-First-Name", required = false) String firstName,
                                                                 @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long resourceId = currentUserResourceService.resolveCurrentResourceId(userId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationDispatchService.sendTestToRecipient(resourceId, firstName, userName));
    }

    @GetMapping("/recipient/{recipientResourceId}")
    @Operation(summary = "Listar notificaciones de un recurso", description = "Por seguridad solo permite consultar el recurso asociado al usuario autenticado")
    public ResponseEntity<List<DispatchResultDTO>> findByRecipient(@PathVariable Long recipientResourceId,
                                                                   @RequestHeader(value = "X-User-Id", required = false) String userId,
                                                                   @RequestHeader(value = "X-User-Email", required = false) String email) {
        currentUserResourceService.assertCurrentUserOwnsResource(userId, email, recipientResourceId);
        return ResponseEntity.ok(notificationDispatchService.findInboxByRecipient(recipientResourceId));
    }
}
