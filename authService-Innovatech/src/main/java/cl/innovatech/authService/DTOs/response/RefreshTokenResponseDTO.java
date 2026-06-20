package cl.innovatech.authService.DTOs.response;

import java.time.LocalDateTime;

public class RefreshTokenResponseDTO {

    private String id;
    private String userId;
    private String token;
    private LocalDateTime expiresAt;
    private Boolean revoked;
    private LocalDateTime revokedAt;
    private LocalDateTime createdAt;
    private Boolean expired;

    public RefreshTokenResponseDTO() {
    }

    public RefreshTokenResponseDTO(String id, String userId, String token, LocalDateTime expiresAt,
                                   Boolean revoked, LocalDateTime revokedAt,
                                   LocalDateTime createdAt, Boolean expired) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
        this.expired = expired;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }
}