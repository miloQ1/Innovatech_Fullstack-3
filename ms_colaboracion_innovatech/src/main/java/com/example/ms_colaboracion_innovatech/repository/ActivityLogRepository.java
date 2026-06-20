package com.example.ms_colaboracion_innovatech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_colaboracion_innovatech.model.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByProjectId(Long projectId);
}
