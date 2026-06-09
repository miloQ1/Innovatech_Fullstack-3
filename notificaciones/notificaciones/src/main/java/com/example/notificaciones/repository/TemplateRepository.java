package com.example.notificaciones.repository;

import com.example.notificaciones.model.Template;
import com.example.notificaciones.model.enums.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByIsActiveTrue();
    Optional<Template> findByEventTypeAndChannelAndIsActiveTrue(String eventType, NotificationChannel channel);
}
