package com.example.ms_recursos_innovatech.config;

import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;

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
 * Crear y eliminar fichas profesionales queda reservado al admin de la plataforma.
 * Editar (PUT/PATCH) también lo puede hacer el dueño de la ficha (employeeCode == X-User-Id),
 * para que cada usuario pueda mantener su propio perfil.
 * El gateway ya valida el JWT y resuelve X-User-Id/X-User-Role a partir de él, por lo que este filtro puede confiar en esos headers.
 */
public class AdminWriteFilter implements Filter {

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final Set<String> EDIT_METHODS = Set.of("PUT", "PATCH");

    private final ProfessionalRepository professionalRepository;

    public AdminWriteFilter(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String method = request.getMethod();

        if (WRITE_METHODS.contains(method)) {
            String role = request.getHeader("X-User-Role");
            boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

            if (!isAdmin) {
                boolean isOwnerEditingOwnProfile = EDIT_METHODS.contains(method) && isOwner(request);
                if (!isOwnerEditingOwnProfile) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Se requiere rol ADMIN para modificar profesionales\"}");
                    return;
                }
            }
        }

        chain.doFilter(req, res);
    }

    private boolean isOwner(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        Long resourceId = extractResourceId(request.getRequestURI());
        if (userId == null || userId.isBlank() || resourceId == null) {
            return false;
        }
        return professionalRepository.findById(resourceId)
                .map(professional -> userId.equals(professional.getEmployeeCode()))
                .orElse(false);
    }

    private Long extractResourceId(String requestUri) {
        String[] segments = requestUri.split("/");
        if (segments.length == 0) {
            return null;
        }
        try {
            return Long.parseLong(segments[segments.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
