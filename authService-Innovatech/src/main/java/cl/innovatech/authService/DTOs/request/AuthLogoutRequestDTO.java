package cl.innovatech.authService.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthLogoutRequestDTO {

    @NotBlank(message = "El refreshToken es obligatorio")
    @Size(max = 500, message = "El refreshToken no puede superar los 500 caracteres")
    private String refreshToken;

    public AuthLogoutRequestDTO() {
    }

    public AuthLogoutRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}