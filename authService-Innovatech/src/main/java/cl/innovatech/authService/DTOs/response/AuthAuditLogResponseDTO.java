package cl.innovatech.authService.DTOs.response;

import java.time.LocalDateTime;

public class AuthAuditLogResponseDTO {

    private String id;
    private String userId;
    private String eventType;
    private String description;
    private String ipAddress;
    private LocalDateTime createdAt;

    public AuthAuditLogResponseDTO() {
    }

    public AuthAuditLogResponseDTO(String id, String userId, String eventType, String description, String ipAddress, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.description = description;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
