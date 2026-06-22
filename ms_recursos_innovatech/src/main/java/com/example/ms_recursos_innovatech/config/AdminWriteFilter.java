package com.example.ms_recursos_innovatech.config;

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
 * Crear/editar/eliminar fichas profesionales queda reservado al admin de la plataforma.
 * Cualquier usuario autenticado puede seguir consultando la lista (GET).
 * El gateway ya valida el JWT y resuelve X-User-Role a partir de él, por lo que este filtro puede confiar en el header.
 */
public class AdminWriteFilter implements Filter {

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (WRITE_METHODS.contains(request.getMethod())) {
            String role = request.getHeader("X-User-Role");
            if (!"ADMIN".equalsIgnoreCase(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Se requiere rol ADMIN para modificar profesionales\"}");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
