package cl.innovatech.authService.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.innovatech.authService.model.AuthAuditLog;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, String> {

    List<AuthAuditLog> findByUserId(String userId);

    List<AuthAuditLog> findByEventType(String eventType);

    List<AuthAuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<AuthAuditLog> findByUserIdOrderByCreatedAtDesc(String userId);

    List<AuthAuditLog> findAllByOrderByCreatedAtDesc();
}
