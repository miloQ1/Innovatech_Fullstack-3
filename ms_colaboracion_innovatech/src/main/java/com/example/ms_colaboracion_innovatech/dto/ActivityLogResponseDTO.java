package com.example.ms_colaboracion_innovatech.dto;

import java.time.LocalDateTime;

public class ActivityLogResponseDTO {

    private Long activityId;
    private Long projectId;
    private Long taskId;
    private Long actorResourceId;
    private String actionType;
    private String description;
    private LocalDateTime createdAt;

    public ActivityLogResponseDTO() {
    }

    public ActivityLogResponseDTO(Long activityId, Long projectId, Long taskId, Long actorResourceId,
            String actionType, String description, LocalDateTime createdAt) {
        this.activityId = activityId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.actorResourceId = actorResourceId;
        this.actionType = actionType;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getActorResourceId() {
        return actorResourceId;
    }

    public void setActorResourceId(Long actorResourceId) {
        this.actorResourceId = actorResourceId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
