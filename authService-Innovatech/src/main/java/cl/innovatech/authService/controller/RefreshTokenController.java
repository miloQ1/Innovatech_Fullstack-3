package cl.innovatech.authService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.innovatech.authService.DTOs.request.RefreshTokenCreateRequestDTO;
import cl.innovatech.authService.DTOs.response.RefreshTokenResponseDTO;
import cl.innovatech.authService.service.RefreshTokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/refresh-tokens")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping
    public ResponseEntity<RefreshTokenResponseDTO> createRefreshToken(
            @Valid @RequestBody RefreshTokenCreateRequestDTO dto
    ) {
        RefreshTokenResponseDTO createdToken = refreshTokenService.createRefreshToken(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdToken);
    }

    @GetMapping
    public ResponseEntity<List<RefreshTokenResponseDTO>> getAllRefreshTokens() {
        return ResponseEntity.ok(refreshTokenService.getAllRefreshTokens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefreshTokenResponseDTO> getRefreshTokenById(@PathVariable String id) {
        return ResponseEntity.ok(refreshTokenService.getRefreshTokenById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RefreshTokenResponseDTO>> getRefreshTokensByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(refreshTokenService.getRefreshTokensByUserId(userId));
    }

    @PatchMapping("/{id}/revoke")
    public ResponseEntity<RefreshTokenResponseDTO> revokeRefreshToken(@PathVariable String id) {
        return ResponseEntity.ok(refreshTokenService.revokeRefreshToken(id));
    }

    @PatchMapping("/user/{userId}/revoke-all")
    public ResponseEntity<Void> revokeAllUserRefreshTokens(@PathVariable String userId) {
        refreshTokenService.revokeAllUserRefreshTokens(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable String id) {
        refreshTokenService.deleteRefreshToken(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/expired")
    public ResponseEntity<Void> deleteExpiredRefreshTokens() {
        refreshTokenService.deleteExpiredRefreshTokens();
        return ResponseEntity.noContent().build();
    }
}
