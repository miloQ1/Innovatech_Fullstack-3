package com.example.notificaciones.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

/**
 * Plantillas: configuración del microservicio, reservada al admin de la plataforma (todos los métodos).
 * Preferencias: cualquier usuario puede leer las suyas (GET /resource/{id} para su propia bandeja),
 * pero solo el admin puede crearlas/editarlas/borrarlas.
 * El gateway ya valida el JWT y resuelve X-User-Role a partir de él, por lo que este filtro puede confiar en el header.
 */
public class AdminRoleFilter implements Filter {

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        boolean isPreferencesRead = request.getRequestURI().contains("/api/preferences") && !WRITE_METHODS.contains(request.getMethod());

        String role = request.getHeader("X-User-Role");
        if (!isPreferencesRead && !"ADMIN".equalsIgnoreCase(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Se requiere rol ADMIN para acceder a este recurso\"}");
            return;
        }

        chain.doFilter(req, res);
    }
}
