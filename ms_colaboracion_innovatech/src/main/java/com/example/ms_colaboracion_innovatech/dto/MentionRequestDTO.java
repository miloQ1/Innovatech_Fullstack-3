package com.example.ms_colaboracion_innovatech.dto;

public class MentionRequestDTO {

    private Long commentId;
    private Long mentionedResourceId;
    private String mentionStatus;

    public MentionRequestDTO() {
    }

    public MentionRequestDTO(Long commentId, Long mentionedResourceId, String mentionStatus) {
        this.commentId = commentId;
        this.mentionedResourceId = mentionedResourceId;
        this.mentionStatus = mentionStatus;
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
}
