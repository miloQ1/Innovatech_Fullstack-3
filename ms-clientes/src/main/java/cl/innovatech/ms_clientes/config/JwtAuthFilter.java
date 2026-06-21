package cl.innovatech.ms_clientes.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cl.innovatech.ms_clientes.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

   @Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    System.out.println("=== Authorization header: " + authHeader);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        System.out.println("=== No Bearer token, skipping");
        filterChain.doFilter(request, response);
        return;
    }

    String token = authHeader.substring(7);
    System.out.println("=== Token válido: " + jwtService.isTokenValid(token));
    System.out.println("=== UserId extraído: " + jwtService.extractUserId(token));

    if (jwtService.isTokenValid(token)) {
        String userId = jwtService.extractUserId(token);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        System.out.println("=== Autenticación seteada para: " + userId);
    }

    filterChain.doFilter(request, response);
}}
