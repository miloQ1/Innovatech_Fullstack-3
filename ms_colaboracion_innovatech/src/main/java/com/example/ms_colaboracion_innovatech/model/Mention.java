package com.example.ms_colaboracion_innovatech.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mentions")
public class Mention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mention_id", nullable = false)
    private Long mentionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "mentioned_resource_id", nullable = false)
    private Long mentionedResourceId;

    @Column(name = "mention_status", nullable = false)
    private String mentionStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Mention() {
    }

    public Mention(Long mentionId, Comment comment, Long mentionedResourceId, String mentionStatus,
            LocalDateTime createdAt) {
        this.mentionId = mentionId;
        this.comment = comment;
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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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
