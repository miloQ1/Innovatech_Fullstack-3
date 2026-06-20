package cl.innovatech.servicio_proyectos.service;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8); // ← igual que authService
    return Keys.hmacShaKeyFor(keyBytes);
}

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
    try {
        extractAllClaims(token);
        return true;
    } catch (Exception e) {
        System.out.println("=== Error validando token: " + e.getMessage());
        System.out.println("=== Tipo de error: " + e.getClass().getName());
        return false;
    }
}
}
