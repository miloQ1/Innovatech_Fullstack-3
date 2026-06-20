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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ProfessionalResponseDTO> create(@RequestBody ProfessionalRequestDTO requestDTO) {
        ProfessionalResponseDTO created = professionalService.save(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar profesional", description = "Actualiza un profesional existente")
    public ResponseEntity<ProfessionalResponseDTO> update(@PathVariable Long id,
            @RequestBody ProfessionalRequestDTO requestDTO) {
        ProfessionalResponseDTO updated = professionalService.update(id, requestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar profesional", description = "Elimina un profesional por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
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
}
