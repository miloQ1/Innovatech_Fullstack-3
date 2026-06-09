package com.example.notificaciones.repository;

import com.example.notificaciones.model.Preference;
import com.example.notificaciones.model.enums.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    List<Preference> findByResourceId(Long resourceId);
    Optional<Preference> findByResourceIdAndChannel(Long resourceId, NotificationChannel channel);
}
