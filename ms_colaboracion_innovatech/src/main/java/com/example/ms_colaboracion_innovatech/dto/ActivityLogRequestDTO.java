package com.example.ms_colaboracion_innovatech.dto;

public class ActivityLogRequestDTO {

    private Long projectId;
    private Long taskId;
    private Long actorResourceId;
    private String actionType;
    private String description;

    public ActivityLogRequestDTO() {
    }

    public ActivityLogRequestDTO(Long projectId, Long taskId, Long actorResourceId, String actionType,
            String description) {
        this.projectId = projectId;
        this.taskId = taskId;
        this.actorResourceId = actorResourceId;
        this.actionType = actionType;
        this.description = description;
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
}
