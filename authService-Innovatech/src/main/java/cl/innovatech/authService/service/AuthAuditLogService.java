package cl.innovatech.authService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.authService.DTOs.request.AuthAuditLogCreateRequestDTO;
import cl.innovatech.authService.DTOs.response.AuthAuditLogResponseDTO;
import cl.innovatech.authService.model.AuthAuditLog;
import cl.innovatech.authService.repository.AuthAuditLogRepository;
import cl.innovatech.authService.repository.UserRepository;

@Service
@Transactional
public class AuthAuditLogService {

    private static final Set<String> ALLOWED_EVENT_TYPES = Set.of(
            "REGISTER_SUCCESS",
            "LOGIN_SUCCESS",
            "LOGIN_FAILED",
            "LOGOUT",
            "TOKEN_REFRESH"
    );

    private final AuthAuditLogRepository authAuditLogRepository;
    private final UserRepository userRepository;

    public AuthAuditLogService(AuthAuditLogRepository authAuditLogRepository, UserRepository userRepository) {
        this.authAuditLogRepository = authAuditLogRepository;
        this.userRepository = userRepository;
    }

    public AuthAuditLogResponseDTO createAuthAuditLog(AuthAuditLogCreateRequestDTO dto) {
        String normalizedEventType = normalizeAndValidateEventType(dto.getEventType());

        if (dto.getUserId() != null && !dto.getUserId().isBlank()) {
            if (!userRepository.existsById(dto.getUserId().trim())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }
        }

        AuthAuditLog authAuditLog = new AuthAuditLog();
        authAuditLog.setUserId(dto.getUserId() != null && !dto.getUserId().isBlank() ? dto.getUserId().trim() : null);
        authAuditLog.setEventType(normalizedEventType);
        authAuditLog.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        authAuditLog.setIpAddress(dto.getIpAddress() != null ? dto.getIpAddress().trim() : null);

        AuthAuditLog savedLog = authAuditLogRepository.save(authAuditLog);
        return mapToResponseDTO(savedLog);
    }

    @Transactional(readOnly = true)
    public List<AuthAuditLogResponseDTO> getAllAuthAuditLogs() {
        return authAuditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AuthAuditLogResponseDTO getAuthAuditLogById(String id) {
        AuthAuditLog authAuditLog = findAuthAuditLogEntityById(id);
        return mapToResponseDTO(authAuditLog);
    }

    @Transactional(readOnly = true)
    public List<AuthAuditLogResponseDTO> getAuthAuditLogsByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        return authAuditLogRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuthAuditLogResponseDTO> getAuthAuditLogsByEventType(String eventType) {
        String normalizedEventType = normalizeAndValidateEventType(eventType);

        return authAuditLogRepository.findByEventType(normalizedEventType)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuthAuditLogResponseDTO> getAuthAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return authAuditLogRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public void deleteAuthAuditLog(String id) {
        AuthAuditLog authAuditLog = findAuthAuditLogEntityById(id);
        authAuditLogRepository.delete(authAuditLog);
    }

    @Transactional(readOnly = true)
    public AuthAuditLog findAuthAuditLogEntityById(String id) {
        return authAuditLogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Log de auditoría no encontrado"));
    }

    private String normalizeAndValidateEventType(String eventType) {
        String normalized = eventType.trim().toUpperCase();

        if (!ALLOWED_EVENT_TYPES.contains(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "EventType inválido. Valores permitidos: REGISTER_SUCCESS, LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT, TOKEN_REFRESH"
            );
        }

        return normalized;
    }

    private AuthAuditLogResponseDTO mapToResponseDTO(AuthAuditLog authAuditLog) {
        AuthAuditLogResponseDTO dto = new AuthAuditLogResponseDTO();
        dto.setId(authAuditLog.getId());
        dto.setUserId(authAuditLog.getUserId());
        dto.setEventType(authAuditLog.getEventType());
        dto.setDescription(authAuditLog.getDescription());
        dto.setIpAddress(authAuditLog.getIpAddress());
        dto.setCreatedAt(authAuditLog.getCreatedAt());
        return dto;
    }
}
