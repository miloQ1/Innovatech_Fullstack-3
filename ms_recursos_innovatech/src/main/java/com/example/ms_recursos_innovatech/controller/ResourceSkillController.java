package com.example.ms_recursos_innovatech.controller;

import com.example.ms_recursos_innovatech.dto.ResourceSkillRequestDTO;
import com.example.ms_recursos_innovatech.dto.ResourceSkillResponseDTO;
import com.example.ms_recursos_innovatech.service.ResourceSkillService;
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
@RequestMapping("/api/resource-skills")
@Tag(name = "Habilidades de recursos", description = "Endpoints para gestionar habilidades asociadas a profesionales")
public class ResourceSkillController {

    private final ResourceSkillService resourceSkillService;

    public ResourceSkillController(ResourceSkillService resourceSkillService) {
        this.resourceSkillService = resourceSkillService;
    }

    @GetMapping
    @Operation(summary = "Listar habilidades de recursos", description = "Obtiene todas las habilidades asociadas")
    public ResponseEntity<List<ResourceSkillResponseDTO>> findAll() {
        return ResponseEntity.ok(resourceSkillService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar habilidad de recurso", description = "Obtiene una habilidad asociada por su ID")
    public ResponseEntity<ResourceSkillResponseDTO> findById(@PathVariable Long id) {
        ResourceSkillResponseDTO response = resourceSkillService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear habilidad de recurso", description = "Asocia una habilidad a un profesional")
    public ResponseEntity<ResourceSkillResponseDTO> create(@RequestBody ResourceSkillRequestDTO requestDTO) {
        ResourceSkillResponseDTO created = resourceSkillService.save(requestDTO);
        if (created == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar habilidad de recurso", description = "Actualiza una habilidad asociada")
    public ResponseEntity<ResourceSkillResponseDTO> update(@PathVariable Long id,
            @RequestBody ResourceSkillRequestDTO requestDTO) {
        ResourceSkillResponseDTO updated = resourceSkillService.update(id, requestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar habilidad de recurso", description = "Elimina una habilidad asociada por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = resourceSkillService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Buscar por recurso", description = "Obtiene habilidades asociadas a un profesional")
    public ResponseEntity<List<ResourceSkillResponseDTO>> findByResourceId(@PathVariable Long resourceId) {
        return ResponseEntity.ok(resourceSkillService.findByResourceId(resourceId));
    }

    @GetMapping("/skill/{skillId}")
    @Operation(summary = "Buscar por habilidad", description = "Obtiene profesionales asociados a una habilidad")
    public ResponseEntity<List<ResourceSkillResponseDTO>> findBySkillId(@PathVariable Long skillId) {
        return ResponseEntity.ok(resourceSkillService.findBySkillId(skillId));
    }
}
