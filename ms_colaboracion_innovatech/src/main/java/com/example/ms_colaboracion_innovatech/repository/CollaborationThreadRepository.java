package com.example.ms_colaboracion_innovatech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_colaboracion_innovatech.model.CollaborationThread;

public interface CollaborationThreadRepository extends JpaRepository<CollaborationThread, Long> {

    List<CollaborationThread> findByProjectId(Long projectId);
}
