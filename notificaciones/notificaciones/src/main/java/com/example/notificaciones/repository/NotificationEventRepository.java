package com.example.notificaciones.repository;

import com.example.notificaciones.model.NotificationEvent;
import com.example.notificaciones.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    List<NotificationEvent> findByEventStatus(EventStatus eventStatus);
    List<NotificationEvent> findByTemplateTemplateId(Long templateId);
}
