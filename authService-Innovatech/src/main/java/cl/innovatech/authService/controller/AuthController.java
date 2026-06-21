package cl.innovatech.authService.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.authService.DTOs.request.AuthLoginRequestDTO;
import cl.innovatech.authService.DTOs.request.AuthLogoutRequestDTO;
import cl.innovatech.authService.DTOs.request.AuthRefreshTokenRequestDTO;
import cl.innovatech.authService.DTOs.request.AuthRegisterRequestDTO;
import cl.innovatech.authService.DTOs.response.AuthResponseDTO;
import cl.innovatech.authService.DTOs.response.UserResponseDTO;
import cl.innovatech.authService.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginRequestDTO request,
                                                 HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        AuthResponseDTO response = authService.login(request, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody AuthRefreshTokenRequestDTO request,
                                                        HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        AuthResponseDTO response = authService.refreshToken(request, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthLogoutRequestDTO request,
                                       HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        authService.logout(request, ipAddress);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(authService.getAuthenticatedUser(authorizationHeader));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getDefaultMessage())
                .findFirst()
                .orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("message", e.getReason() != null ? e.getReason() : "Error en la solicitud"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}