package com.example.ms_colaboracion_innovatech.dto;

public class CollaborationThreadRequestDTO {

    private Long projectId;
    private Long taskId;
    private String title;
    private String status;
    private Long createdByResourceId;

    public CollaborationThreadRequestDTO() {
    }

        public CollaborationThreadRequestDTO(Long projectId, Long taskId, String title, String status,
            Long createdByResourceId) {
        this.projectId = projectId;
        this.taskId = taskId;
        this.title = title;
        this.status = status;
        this.createdByResourceId = createdByResourceId;
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
}
