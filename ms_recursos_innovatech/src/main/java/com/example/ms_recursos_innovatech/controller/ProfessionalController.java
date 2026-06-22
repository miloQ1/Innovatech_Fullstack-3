package com.example.ms_recursos_innovatech.controller;

import com.example.ms_recursos_innovatech.dto.ProfessionalRequestDTO;
import com.example.ms_recursos_innovatech.dto.ProfessionalResponseDTO;
import com.example.ms_recursos_innovatech.service.ProfessionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/professionals")
@Tag(name = "Profesionales", description = "Endpoints para gestionar profesionales del microservicio de recursos")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping
    @Operation(summary = "Listar profesionales", description = "Obtiene todos los profesionales registrados")
    public ResponseEntity<List<ProfessionalResponseDTO>> findAll() {
        return ResponseEntity.ok(professionalService.findAll());
    }

    @GetMapping("/me")
    @Operation(summary = "Mi ficha profesional", description = "Obtiene la ficha profesional vinculada al usuario autenticado (employeeCode = userId)")
    public ResponseEntity<ProfessionalResponseDTO> findMe(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        ProfessionalResponseDTO response = professionalService.findByEmployeeCode(userId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi ficha profesional", description = "Permite al usuario autenticado editar solo su propia ficha profesional")
    public ResponseEntity<ProfessionalResponseDTO> updateMe(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                            @RequestBody ProfessionalRequestDTO requestDTO) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario autenticado requerido");
        }
        ProfessionalResponseDTO updated = professionalService.updateByEmployeeCode(userId, requestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }


    @PostMapping("/me")
    @Operation(summary = "Crear mi ficha profesional", description = "Permite al usuario autenticado crear su propia ficha si todavía no existe")
    public ResponseEntity<ProfessionalResponseDTO> createMe(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                            @RequestHeader(value = "X-User-Email", required = false) String email,
                                                            @RequestHeader(value = "X-User-First-Name", required = false) String firstName,
                                                            @RequestHeader(value = "X-User-Last-Name", required = false) String lastName,
                                                            @RequestBody ProfessionalRequestDTO requestDTO) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario autenticado requerido");
        }

        requestDTO.setEmployeeCode(userId);
        if ((requestDTO.getEmail() == null || requestDTO.getEmail().isBlank()) && email != null) {
            requestDTO.setEmail(email);
        }
        if ((requestDTO.getFirstName() == null || requestDTO.getFirstName().isBlank()) && firstName != null) {
            requestDTO.setFirstName(firstName);
        }
        if ((requestDTO.getLastName() == null || requestDTO.getLastName().isBlank()) && lastName != null) {
            requestDTO.setLastName(lastName);
        }

        ProfessionalResponseDTO existing = professionalService.findByEmployeeCode(userId);
        if (existing != null) {
            ProfessionalResponseDTO updated = professionalService.updateByEmployeeCode(userId, requestDTO);
            return ResponseEntity.ok(updated);
        }

        ProfessionalResponseDTO created = professionalService.save(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profesional", description = "Obtiene un profesional por su ID")
    public ResponseEntity<ProfessionalResponseDTO> findById(@PathVariable Long id) {
        ProfessionalResponseDTO response = professionalService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear profesional", description = "Crea un profesional con la informacion enviada")
    public ResponseEntity<ProfessionalResponseDTO> create(@RequestHeader(value = "X-User-Role", required = false) String role,
                                                          @RequestBody ProfessionalRequestDTO requestDTO) {
        assertAdmin(role, "crear profesionales");
        ProfessionalResponseDTO created = professionalService.save(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar profesional", description = "El admin puede editar cualquier profesional; un usuario normal solo puede editar su propia ficha")
    public ResponseEntity<ProfessionalResponseDTO> update(@PathVariable Long id,
                                                          @RequestHeader(value = "X-User-Id", required = false) String userId,
                                                          @RequestHeader(value = "X-User-Role", required = false) String role,
                                                          @RequestBody ProfessionalRequestDTO requestDTO) {
        ProfessionalResponseDTO current = professionalService.findById(id);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }
        assertAdminOrOwner(role, userId, current.getEmployeeCode());
        ProfessionalResponseDTO updated = professionalService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar profesional", description = "Elimina un profesional por ID. Solo ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role, "eliminar profesionales");
        boolean deleted = professionalService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar por estado", description = "Obtiene profesionales por estado")
    public ResponseEntity<List<ProfessionalResponseDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(professionalService.findByStatus(status));
    }

    @GetMapping("/role/{roleName}")
    @Operation(summary = "Filtrar por rol", description = "Obtiene profesionales por rol")
    public ResponseEntity<List<ProfessionalResponseDTO>> findByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(professionalService.findByRoleName(roleName));
    }

    @GetMapping("/seniority/{seniority}")
    @Operation(summary = "Filtrar por seniority", description = "Obtiene profesionales por seniority")
    public ResponseEntity<List<ProfessionalResponseDTO>> findBySeniority(@PathVariable String seniority) {
        return ResponseEntity.ok(professionalService.findBySeniority(seniority));
    }

    private void assertAdminOrOwner(String role, String userId, String employeeCode) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return;
        }
        if (userId != null && !userId.isBlank() && userId.equals(employeeCode)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes editar tu propia ficha profesional");
    }

    private void assertAdmin(String role, String action) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere rol ADMIN para " + action);
        }
    }
}
