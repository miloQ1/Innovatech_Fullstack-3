package com.example.ms_recursos_innovatech.controller;

import com.example.ms_recursos_innovatech.dto.AvailabilityRequestDTO;
import com.example.ms_recursos_innovatech.dto.AvailabilityResponseDTO;
import com.example.ms_recursos_innovatech.service.AvailabilityService;
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
@RequestMapping("/api/availability")
@Tag(name = "Disponibilidad", description = "Endpoints para gestionar disponibilidad de profesionales")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    @Operation(summary = "Listar disponibilidad", description = "Obtiene toda la disponibilidad registrada")
    public ResponseEntity<List<AvailabilityResponseDTO>> findAll() {
        return ResponseEntity.ok(availabilityService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar disponibilidad", description = "Obtiene una disponibilidad por su ID")
    public ResponseEntity<AvailabilityResponseDTO> findById(@PathVariable Long id) {
        AvailabilityResponseDTO response = availabilityService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear disponibilidad", description = "Crea una disponibilidad para un profesional")
    public ResponseEntity<AvailabilityResponseDTO> create(@RequestBody AvailabilityRequestDTO requestDTO) {
        AvailabilityResponseDTO created = availabilityService.save(requestDTO);
        if (created == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar disponibilidad", description = "Actualiza una disponibilidad existente")
    public ResponseEntity<AvailabilityResponseDTO> update(@PathVariable Long id,
            @RequestBody AvailabilityRequestDTO requestDTO) {
        AvailabilityResponseDTO updated = availabilityService.update(id, requestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar disponibilidad", description = "Elimina una disponibilidad por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = availabilityService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Buscar por recurso", description = "Obtiene disponibilidad por profesional")
    public ResponseEntity<List<AvailabilityResponseDTO>> findByResourceId(@PathVariable Long resourceId) {
        return ResponseEntity.ok(availabilityService.findByResourceId(resourceId));
    }

    @GetMapping("/type/{availabilityType}")
    @Operation(summary = "Buscar por tipo", description = "Obtiene disponibilidad por tipo")
    public ResponseEntity<List<AvailabilityResponseDTO>> findByAvailabilityType(
            @PathVariable String availabilityType) {
        return ResponseEntity.ok(availabilityService.findByAvailabilityType(availabilityType));
    }
}
