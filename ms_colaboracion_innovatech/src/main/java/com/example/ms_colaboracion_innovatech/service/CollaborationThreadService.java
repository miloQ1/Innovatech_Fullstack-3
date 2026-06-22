package com.example.ms_colaboracion_innovatech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.example.ms_colaboracion_innovatech.dto.CollaborationThreadRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.CollaborationThreadResponseDTO;
import com.example.ms_colaboracion_innovatech.model.CollaborationThread;
import com.example.ms_colaboracion_innovatech.repository.CollaborationThreadRepository;

@Service
public class CollaborationThreadService {

    private final CollaborationThreadRepository threadRepository;
    private final NotificationPublisherService notificationPublisherService;

    public CollaborationThreadService(CollaborationThreadRepository threadRepository,
                                       NotificationPublisherService notificationPublisherService) {
        this.threadRepository = threadRepository;
        this.notificationPublisherService = notificationPublisherService;
    }

    public List<CollaborationThreadResponseDTO> findAll() {
        return threadRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CollaborationThreadResponseDTO findById(Long id) {
        return threadRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public CollaborationThreadResponseDTO save(CollaborationThreadRequestDTO requestDTO) {
        CollaborationThread thread = new CollaborationThread();
        thread.setProjectId(requestDTO.getProjectId());
        thread.setTaskId(requestDTO.getTaskId());
        thread.setTitle(requestDTO.getTitle());
        thread.setStatus(requestDTO.getStatus());
        thread.setCreatedByResourceId(requestDTO.getCreatedByResourceId());
        LocalDateTime now = LocalDateTime.now();
        thread.setCreatedAt(now);
        thread.setUpdatedAt(now);
        CollaborationThread saved = threadRepository.save(thread);

        notificationPublisherService.notifyProject(saved.getProjectId(), "THREAD_CREATED", saved.getCreatedByResourceId(), Map.of(
            "threadTitle", saved.getTitle()
        ));

        return toResponse(saved);
    }

    public CollaborationThreadResponseDTO update(Long id, CollaborationThreadRequestDTO requestDTO) {
        CollaborationThread thread = threadRepository.findById(id).orElse(null);
        if (thread == null) {
            return null;
        }
        thread.setProjectId(requestDTO.getProjectId());
        thread.setTaskId(requestDTO.getTaskId());
        thread.setTitle(requestDTO.getTitle());
        thread.setStatus(requestDTO.getStatus());
        thread.setCreatedByResourceId(requestDTO.getCreatedByResourceId());
        thread.setUpdatedAt(LocalDateTime.now());
        return toResponse(threadRepository.save(thread));
    }

    public boolean delete(Long id) {
        if (!threadRepository.existsById(id)) {
            return false;
        }
        threadRepository.deleteById(id);
        return true;
    }

    public List<CollaborationThreadResponseDTO> findByProjectId(Long projectId) {
        return threadRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    private CollaborationThreadResponseDTO toResponse(CollaborationThread thread) {
        return new CollaborationThreadResponseDTO(
                thread.getThreadId(),
                thread.getProjectId(),
                thread.getTaskId(),
                thread.getTitle(),
                thread.getStatus(),
                thread.getCreatedByResourceId(),
                thread.getCreatedAt(),
                thread.getUpdatedAt());
    }
}
