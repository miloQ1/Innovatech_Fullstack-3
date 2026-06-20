package com.example.ms_colaboracion_innovatech.dto;

import java.time.LocalDateTime;

public class CollaborationThreadResponseDTO {

    private Long threadId;
    private Long projectId;
    private Long taskId;
    private String title;
    private String status;
    private Long createdByResourceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CollaborationThreadResponseDTO() {
    }

        public CollaborationThreadResponseDTO(Long threadId, Long projectId, Long taskId, String title,
            String status, Long createdByResourceId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.threadId = threadId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.title = title;
        this.status = status;
        this.createdByResourceId = createdByResourceId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedByResourceId() {
        return createdByResourceId;
    }

    public void setCreatedByResourceId(Long createdByResourceId) {
        this.createdByResourceId = createdByResourceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
