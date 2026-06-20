package com.example.ms_colaboracion_innovatech.dto;

import java.time.LocalDateTime;

public class MentionResponseDTO {

    private Long mentionId;
    private Long commentId;
    private Long mentionedResourceId;
    private String mentionStatus;
    private LocalDateTime createdAt;

    public MentionResponseDTO() {
    }

    public MentionResponseDTO(Long mentionId, Long commentId, Long mentionedResourceId,
            String mentionStatus, LocalDateTime createdAt) {
        this.mentionId = mentionId;
        this.commentId = commentId;
        this.mentionedResourceId = mentionedResourceId;
        this.mentionStatus = mentionStatus;
        this.createdAt = createdAt;
    }

    public Long getMentionId() {
        return mentionId;
    }

    public void setMentionId(Long mentionId) {
        this.mentionId = mentionId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getMentionedResourceId() {
        return mentionedResourceId;
    }

    public void setMentionedResourceId(Long mentionedResourceId) {
        this.mentionedResourceId = mentionedResourceId;
    }

    public String getMentionStatus() {
        return mentionStatus;
    }

    public void setMentionStatus(String mentionStatus) {
        this.mentionStatus = mentionStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
