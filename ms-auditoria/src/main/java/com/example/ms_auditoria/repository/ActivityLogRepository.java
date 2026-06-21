package com.example.ms_auditoria.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_auditoria.model.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByProjectId(Long projectId);
}
