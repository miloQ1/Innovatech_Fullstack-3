package cl.innovatech.authService.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.authService.DTOs.request.AuthLoginRequestDTO;
import cl.innovatech.authService.DTOs.request.AuthLogoutRequestDTO;
import cl.innovatech.authService.DTOs.request.AuthRefreshTokenRequestDTO;
import cl.innovatech.authService.DTOs.request.AuthRegisterRequestDTO;
import cl.innovatech.authService.DTOs.response.AuthResponseDTO;
import cl.innovatech.authService.DTOs.response.UserResponseDTO;
import cl.innovatech.authService.model.AuthAuditLog;
import cl.innovatech.authService.model.RefreshToken;
import cl.innovatech.authService.model.User;
import cl.innovatech.authService.repository.AuthAuditLogRepository;
import cl.innovatech.authService.repository.RefreshTokenRepository;
import cl.innovatech.authService.repository.UserRepository;


@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthAuditLogRepository authAuditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;


    @Value("${jwt.refresh-token-expiration-days}")
    private Long refreshTokenExpirationDays;

    public AuthService(UserRepository userRepository,
                   RefreshTokenRepository refreshTokenRepository,
                   AuthAuditLogRepository authAuditLogRepository,
                   PasswordEncoder passwordEncoder,
                   JwtService jwtService,
                   RestTemplate restTemplate) {
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.authAuditLogRepository = authAuditLogRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.restTemplate = restTemplate;  
}

    public AuthResponseDTO register(AuthRegisterRequestDTO dto) {
        String userName = dto.getUserName().trim();
        String email = dto.getEmail().trim().toLowerCase();

        if (userRepository.existsByUserName(userName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El userName ya está registrado");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        User user = new User();
        user.setUserName(userName);
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setStatus("ACTIVE");
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        try {
        Map<String, Object> professional = new HashMap<>();
        professional.put("firstName", savedUser.getFirstName());
        professional.put("lastName",  savedUser.getLastName());
        professional.put("email",     savedUser.getEmail());
        professional.put("employeeCode", savedUser.getId());
        professional.put("status",    "ACTIVE");
        professional.put("weeklyCapacityHours", 40);
        
        restTemplate.postForObject(
            "http://localhost:8083/api/professionals",
            professional,
            Object.class
        );
    } catch (Exception e) {
        // Si falla no interrumpir el registro
        System.out.println("Warning: no se pudo crear perfil en recursos: " + e.getMessage());
    }

        String accessToken = jwtService.generateAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser.getId());

        createAuditLog(savedUser.getId(), "REGISTER_SUCCESS", "Registro exitoso de usuario", null);

        return buildAuthResponse(savedUser, accessToken, refreshToken.getToken());
    }

    public AuthResponseDTO login(AuthLoginRequestDTO dto, String ipAddress) {
        String identifier = dto.getIdentifier().trim();

        User user = findUserByIdentifier(identifier);

        if (!Boolean.TRUE.equals(user.getEnabled()) || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            createAuditLog(user.getId(), "LOGIN_FAILED", "Intento de login con cuenta inactiva o bloqueada", ipAddress);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La cuenta no está habilitada");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            createAuditLog(user.getId(), "LOGIN_FAILED", "Contraseña incorrecta", ipAddress);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        user.setLastLoginAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(updatedUser);
        RefreshToken refreshToken = createRefreshToken(updatedUser.getId());

        createAuditLog(updatedUser.getId(), "LOGIN_SUCCESS", "Inicio de sesión exitoso", ipAddress);

        return buildAuthResponse(updatedUser, accessToken, refreshToken.getToken());
    }

    public AuthResponseDTO refreshToken(AuthRefreshTokenRequestDTO dto, String ipAddress) {
        String tokenValue = dto.getRefreshToken().trim();

        RefreshToken currentRefreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"));

        if (Boolean.TRUE.equals(currentRefreshToken.getRevoked())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El refresh token fue revocado");
        }

        if (currentRefreshToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El refresh token expiró");
        }

        User user = userRepository.findById(currentRefreshToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        currentRefreshToken.setRevoked(true);
        currentRefreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(currentRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(user);
        RefreshToken newRefreshToken = createRefreshToken(user.getId());

        createAuditLog(user.getId(), "TOKEN_REFRESH", "Renovación exitosa de token", ipAddress);

        return buildAuthResponse(user, newAccessToken, newRefreshToken.getToken());
    }

    public void logout(AuthLogoutRequestDTO dto, String ipAddress) {
        String tokenValue = dto.getRefreshToken().trim();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token no encontrado"));

        if (!Boolean.TRUE.equals(refreshToken.getRevoked())) {
            refreshToken.setRevoked(true);
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
        }

        createAuditLog(refreshToken.getUserId(), "LOGOUT", "Cierre de sesión exitoso", ipAddress);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getAuthenticatedUser(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);

        if (!jwtService.isTokenValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }

        String userId = jwtService.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return mapToUserResponseDTO(user);
    }

    private User findUserByIdentifier(String identifier) {
        return userRepository.findByUserName(identifier)
                .or(() -> userRepository.findByEmail(identifier.toLowerCase()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
    }

    private RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(generateSecureToken());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    private void createAuditLog(String userId, String eventType, String description, String ipAddress) {
        AuthAuditLog log = new AuthAuditLog();
        log.setUserId(userId);
        log.setEventType(eventType);
        log.setDescription(description);
        log.setIpAddress(ipAddress);

        authAuditLogRepository.save(log);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header Authorization requerido");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Formato de token inválido");
        }

        return authorizationHeader.substring(7);
    }

    private AuthResponseDTO buildAuthResponse(User user, String accessToken, String refreshToken) {
        AuthResponseDTO responseDTO = new AuthResponseDTO();
        responseDTO.setAccessToken(accessToken);
        responseDTO.setRefreshToken(refreshToken);
        responseDTO.setTokenType("Bearer");
        responseDTO.setExpiresIn(jwtService.getAccessTokenExpirationSeconds());
        responseDTO.setUser(mapToUserResponseDTO(user));
        return responseDTO;
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setEnabled(user.getEnabled());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}