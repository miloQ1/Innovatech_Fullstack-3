package cl.innovatech.authService.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.authService.DTOs.request.RefreshTokenCreateRequestDTO;
import cl.innovatech.authService.DTOs.response.RefreshTokenResponseDTO;
import cl.innovatech.authService.model.RefreshToken;
import cl.innovatech.authService.repository.RefreshTokenRepository;
import cl.innovatech.authService.repository.UserRepository;


@Service
@Transactional
public class RefreshTokenService {

    private static final int DEFAULT_EXPIRATION_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshTokenResponseDTO createRefreshToken(RefreshTokenCreateRequestDTO dto) {
        String userId = dto.getUserId().trim();

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(generateSecureToken());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(DEFAULT_EXPIRATION_DAYS));
        refreshToken.setRevoked(false);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        return mapToResponseDTO(savedToken);
    }

    @Transactional(readOnly = true)
    public List<RefreshTokenResponseDTO> getAllRefreshTokens() {
        return refreshTokenRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponseDTO getRefreshTokenById(String id) {
        RefreshToken refreshToken = findRefreshTokenEntityById(id);
        return mapToResponseDTO(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponseDTO getRefreshTokenByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token no encontrado"));

        return mapToResponseDTO(refreshToken);
    }

    @Transactional(readOnly = true)
    public List<RefreshTokenResponseDTO> getRefreshTokensByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        return refreshTokenRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public RefreshTokenResponseDTO revokeRefreshToken(String id) {
        RefreshToken refreshToken = findRefreshTokenEntityById(id);

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El refresh token ya fue revocado");
        }

        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(LocalDateTime.now());

        RefreshToken updatedToken = refreshTokenRepository.save(refreshToken);
        return mapToResponseDTO(updatedToken);
    }

    public void revokeAllUserRefreshTokens(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);

        for (RefreshToken token : activeTokens) {
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
        }

        refreshTokenRepository.saveAll(activeTokens);
    }

    public void deleteRefreshToken(String id) {
        RefreshToken refreshToken = findRefreshTokenEntityById(id);
        refreshTokenRepository.delete(refreshToken);
    }

    public void deleteExpiredRefreshTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public RefreshToken findRefreshTokenEntityById(String id) {
        return refreshTokenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token no encontrado"));
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private RefreshTokenResponseDTO mapToResponseDTO(RefreshToken refreshToken) {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO();
        dto.setId(refreshToken.getId());
        dto.setUserId(refreshToken.getUserId());
        dto.setToken(refreshToken.getToken());
        dto.setExpiresAt(refreshToken.getExpiresAt());
        dto.setRevoked(refreshToken.getRevoked());
        dto.setRevokedAt(refreshToken.getRevokedAt());
        dto.setCreatedAt(refreshToken.getCreatedAt());
        dto.setExpired(refreshToken.isExpired());
        return dto;
    }
}
