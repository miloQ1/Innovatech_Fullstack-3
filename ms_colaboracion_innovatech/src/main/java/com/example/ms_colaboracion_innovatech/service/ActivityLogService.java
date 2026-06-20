package com.example.ms_colaboracion_innovatech.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.ms_colaboracion_innovatech.dto.ActivityLogRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.ActivityLogResponseDTO;
import com.example.ms_colaboracion_innovatech.model.ActivityLog;
import com.example.ms_colaboracion_innovatech.repository.ActivityLogRepository;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public List<ActivityLogResponseDTO> findAll() {
        return activityLogRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ActivityLogResponseDTO findById(Long id) {
        return activityLogRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public ActivityLogResponseDTO save(ActivityLogRequestDTO requestDTO) {
        ActivityLog activity = new ActivityLog();
        activity.setProjectId(requestDTO.getProjectId());
        activity.setTaskId(requestDTO.getTaskId());
        activity.setActorResourceId(requestDTO.getActorResourceId());
        activity.setActionType(requestDTO.getActionType());
        activity.setDescription(requestDTO.getDescription());
        activity.setCreatedAt(LocalDateTime.now());
        return toResponse(activityLogRepository.save(activity));
    }

    public ActivityLogResponseDTO update(Long id, ActivityLogRequestDTO requestDTO) {
        ActivityLog activity = activityLogRepository.findById(id).orElse(null);
        if (activity == null) {
            return null;
        }
        activity.setProjectId(requestDTO.getProjectId());
        activity.setTaskId(requestDTO.getTaskId());
        activity.setActorResourceId(requestDTO.getActorResourceId());
        activity.setActionType(requestDTO.getActionType());
        activity.setDescription(requestDTO.getDescription());
        return toResponse(activityLogRepository.save(activity));
    }

    public boolean delete(Long id) {
        if (!activityLogRepository.existsById(id)) {
            return false;
        }
        activityLogRepository.deleteById(id);
        return true;
    }

    public List<ActivityLogResponseDTO> findByProjectId(Long projectId) {
        return activityLogRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    private ActivityLogResponseDTO toResponse(ActivityLog activity) {
        return new ActivityLogResponseDTO(
                activity.getActivityId(),
                activity.getProjectId(),
                activity.getTaskId(),
                activity.getActorResourceId(),
                activity.getActionType(),
                activity.getDescription(),
                activity.getCreatedAt());
    }
}
