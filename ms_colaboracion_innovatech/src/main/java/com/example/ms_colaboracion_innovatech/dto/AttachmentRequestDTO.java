package com.example.ms_colaboracion_innovatech.dto;

public class AttachmentRequestDTO {

    private Long commentId;
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private Long sizeBytes;

    public AttachmentRequestDTO() {
    }

    public AttachmentRequestDTO(Long commentId, String fileName, String fileUrl, String mimeType, Long sizeBytes) {
        this.commentId = commentId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
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
}
