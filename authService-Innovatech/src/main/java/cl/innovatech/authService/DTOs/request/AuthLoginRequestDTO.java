package cl.innovatech.authService.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthLoginRequestDTO {

    @NotBlank(message = "El identificador es obligatorio")
    @Size(max = 120, message = "El identificador no puede superar los 120 caracteres")
    private String identifier;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 100, message = "La contraseña no puede superar los 100 caracteres")
    private String password;

    public AuthLoginRequestDTO() {
    }

    public AuthLoginRequestDTO(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}