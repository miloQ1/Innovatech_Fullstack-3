package com.example.ms_colaboracion_innovatech.dto;

public class CommentRequestDTO {

    private Long threadId;
    private Long authorResourceId;
    private String content;

    public CommentRequestDTO() {
    }

    public CommentRequestDTO(Long threadId, Long authorResourceId, String content) {
        this.threadId = threadId;
        this.authorResourceId = authorResourceId;
        this.content = content;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getAuthorResourceId() {
        return authorResourceId;
    }

    public void setAuthorResourceId(Long authorResourceId) {
        this.authorResourceId = authorResourceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
