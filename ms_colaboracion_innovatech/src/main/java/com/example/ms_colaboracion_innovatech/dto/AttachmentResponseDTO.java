package com.example.ms_colaboracion_innovatech.dto;

import java.time.LocalDateTime;

public class AttachmentResponseDTO {

    private Long attachmentId;
    private Long commentId;
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;

    public AttachmentResponseDTO() {
    }

    public AttachmentResponseDTO(Long attachmentId, Long commentId, String fileName, String fileUrl,
            String mimeType, Long sizeBytes, LocalDateTime uploadedAt) {
        this.attachmentId = attachmentId;
        this.commentId = commentId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.uploadedAt = uploadedAt;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
