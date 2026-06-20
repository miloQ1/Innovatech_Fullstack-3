package com.example.ms_recursos_innovatech.controller;

import com.example.ms_recursos_innovatech.dto.AbsenceRequestDTO;
import com.example.ms_recursos_innovatech.dto.AbsenceResponseDTO;
import com.example.ms_recursos_innovatech.service.AbsenceService;
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
@RequestMapping("/api/absences")
@Tag(name = "Ausencias", description = "Endpoints para gestionar ausencias de profesionales")
public class AbsenceController {

    private final AbsenceService absenceService;

    public AbsenceController(AbsenceService absenceService) {
        this.absenceService = absenceService;
    }

    @GetMapping
    @Operation(summary = "Listar ausencias", description = "Obtiene todas las ausencias registradas")
    public ResponseEntity<List<AbsenceResponseDTO>> findAll() {
        return ResponseEntity.ok(absenceService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ausencia", description = "Obtiene una ausencia por su ID")
    public ResponseEntity<AbsenceResponseDTO> findById(@PathVariable Long id) {
        AbsenceResponseDTO response = absenceService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear ausencia", description = "Crea una ausencia para un profesional")
    public ResponseEntity<AbsenceResponseDTO> create(@RequestBody AbsenceRequestDTO requestDTO) {
        AbsenceResponseDTO created = absenceService.save(requestDTO);
        if (created == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ausencia", description = "Actualiza una ausencia existente")
    public ResponseEntity<AbsenceResponseDTO> update(@PathVariable Long id,
            @RequestBody AbsenceRequestDTO requestDTO) {
        AbsenceResponseDTO updated = absenceService.update(id, requestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ausencia", description = "Elimina una ausencia por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = absenceService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Buscar por recurso", description = "Obtiene ausencias por profesional")
    public ResponseEntity<List<AbsenceResponseDTO>> findByResourceId(@PathVariable Long resourceId) {
        return ResponseEntity.ok(absenceService.findByResourceId(resourceId));
    }

    @GetMapping("/type/{absenceType}")
    @Operation(summary = "Buscar por tipo", description = "Obtiene ausencias por tipo")
    public ResponseEntity<List<AbsenceResponseDTO>> findByAbsenceType(@PathVariable String absenceType) {
        return ResponseEntity.ok(absenceService.findByAbsenceType(absenceType));
    }
}
