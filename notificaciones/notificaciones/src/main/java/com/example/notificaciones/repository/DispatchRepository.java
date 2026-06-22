package com.example.notificaciones.repository;

import com.example.notificaciones.model.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
    List<Dispatch> findByNotificationEventEventId(Long eventId);
    List<Dispatch> findByRecipientResourceId(Long recipientResourceId);
    List<Dispatch> findByRecipientResourceIdOrderBySentAtDesc(Long recipientResourceId);
    boolean existsByRecipientResourceIdAndNotificationEventEventType(Long recipientResourceId, String eventType);
}
