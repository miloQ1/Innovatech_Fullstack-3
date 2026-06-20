package cl.innovatech.authService.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.innovatech.authService.DTOs.request.AuthAuditLogCreateRequestDTO;
import cl.innovatech.authService.DTOs.response.AuthAuditLogResponseDTO;
import cl.innovatech.authService.service.AuthAuditLogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth-audit-logs")
public class AuthAuditLogController {

    private final AuthAuditLogService authAuditLogService;

    public AuthAuditLogController(AuthAuditLogService authAuditLogService) {
        this.authAuditLogService = authAuditLogService;
    }

    @PostMapping
    public ResponseEntity<AuthAuditLogResponseDTO> createAuthAuditLog(
            @Valid @RequestBody AuthAuditLogCreateRequestDTO dto
    ) {
        AuthAuditLogResponseDTO createdLog = authAuditLogService.createAuthAuditLog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
    }

    @GetMapping
    public ResponseEntity<List<AuthAuditLogResponseDTO>> getAllAuthAuditLogs() {
        return ResponseEntity.ok(authAuditLogService.getAllAuthAuditLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthAuditLogResponseDTO> getAuthAuditLogById(@PathVariable String id) {
        return ResponseEntity.ok(authAuditLogService.getAuthAuditLogById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuthAuditLogResponseDTO>> getAuthAuditLogsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(authAuditLogService.getAuthAuditLogsByUserId(userId));
    }

    @GetMapping("/event/{eventType}")
    public ResponseEntity<List<AuthAuditLogResponseDTO>> getAuthAuditLogsByEventType(@PathVariable String eventType) {
        return ResponseEntity.ok(authAuditLogService.getAuthAuditLogsByEventType(eventType));
    }

    @GetMapping("/range")
    public ResponseEntity<List<AuthAuditLogResponseDTO>> getAuthAuditLogsByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(authAuditLogService.getAuthAuditLogsByDateRange(start, end));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthAuditLog(@PathVariable String id) {
        authAuditLogService.deleteAuthAuditLog(id);
        return ResponseEntity.noContent().build();
    }
}
