package cl.innovatech.authService.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthAuditLogCreateRequestDTO {

    @Size(max = 50, message = "El userId no puede superar los 50 caracteres")
    private String userId;

    @NotBlank(message = "El eventType es obligatorio")
    @Size(max = 50, message = "El eventType no puede superar los 50 caracteres")
    private String eventType;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;

    @Size(max = 100, message = "La ipAddress no puede superar los 100 caracteres")
    private String ipAddress;

    public AuthAuditLogCreateRequestDTO() {
    }

    public AuthAuditLogCreateRequestDTO(String userId, String eventType, String description, String ipAddress) {
        this.userId = userId;
        this.eventType = eventType;
        this.description = description;
        this.ipAddress = ipAddress;
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
}
