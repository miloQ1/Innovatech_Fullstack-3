package cl.innovatech.authService.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RefreshTokenCreateRequestDTO {

    @NotBlank(message = "El userId es obligatorio")
    @Size(max = 50, message = "El userId no puede superar los 50 caracteres")
    private String userId;

    public RefreshTokenCreateRequestDTO() {
    }

    public RefreshTokenCreateRequestDTO(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
