package com.example.ms_recursos_innovatech.controller;

import com.example.ms_recursos_innovatech.dto.AssignmentRequestDTO;
import com.example.ms_recursos_innovatech.dto.AssignmentResponseDTO;
import com.example.ms_recursos_innovatech.service.AssignmentService;
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
@RequestMapping("/api/assignments")
@Tag(name = "Asignaciones", description = "Endpoints para gestionar asignaciones a proyectos")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    @Operation(summary = "Listar asignaciones", description = "Obtiene todas las asignaciones registradas")
    public ResponseEntity<List<AssignmentResponseDTO>> findAll() {
        return ResponseEntity.ok(assignmentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar asignacion", description = "Obtiene una asignacion por su ID")
    public ResponseEntity<AssignmentResponseDTO> findById(@PathVariable Long id) {
        AssignmentResponseDTO response = assignmentService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear asignacion", description = "Crea una asignacion para un profesional")
    public ResponseEntity<AssignmentResponseDTO> create(@RequestBody AssignmentRequestDTO requestDTO) {
        AssignmentResponseDTO created = assignmentService.save(requestDTO);
        if (created == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asignacion", description = "Actualiza una asignacion existente")
    public ResponseEntity<AssignmentResponseDTO> update(@PathVariable Long id,
            @RequestBody AssignmentRequestDTO requestDTO) {
        AssignmentResponseDTO updated = assignmentService.update(id, requestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asignacion", description = "Elimina una asignacion por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = assignmentService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Buscar por recurso", description = "Obtiene asignaciones por profesional")
    public ResponseEntity<List<AssignmentResponseDTO>> findByResourceId(@PathVariable Long resourceId) {
        return ResponseEntity.ok(assignmentService.findByResourceId(resourceId));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Buscar por proyecto", description = "Obtiene asignaciones por proyecto")
    public ResponseEntity<List<AssignmentResponseDTO>> findByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(assignmentService.findByProjectId(projectId));
    }

    @GetMapping("/status/{assignmentStatus}")
    @Operation(summary = "Buscar por estado", description = "Obtiene asignaciones por estado")
    public ResponseEntity<List<AssignmentResponseDTO>> findByAssignmentStatus(
            @PathVariable String assignmentStatus) {
        return ResponseEntity.ok(assignmentService.findByAssignmentStatus(assignmentStatus));
    }
}
