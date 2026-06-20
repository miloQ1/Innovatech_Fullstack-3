package com.example.ms_recursos_innovatech.controller;

import com.example.ms_recursos_innovatech.dto.SkillRequestDTO;
import com.example.ms_recursos_innovatech.dto.SkillResponseDTO;
import com.example.ms_recursos_innovatech.service.SkillService;
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
@RequestMapping("/api/skills")
@Tag(name = "Habilidades", description = "Endpoints para gestionar habilidades del microservicio de recursos")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(summary = "Listar habilidades", description = "Obtiene todas las habilidades registradas")
    public ResponseEntity<List<SkillResponseDTO>> findAll() {
        return ResponseEntity.ok(skillService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar habilidad", description = "Obtiene una habilidad por su ID")
    public ResponseEntity<SkillResponseDTO> findById(@PathVariable Long id) {
        SkillResponseDTO response = skillService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear habilidad", description = "Crea una habilidad con la informacion enviada")
    public ResponseEntity<SkillResponseDTO> create(@RequestBody SkillRequestDTO requestDTO) {
        SkillResponseDTO created = skillService.save(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar habilidad", description = "Actualiza una habilidad existente")
    public ResponseEntity<SkillResponseDTO> update(@PathVariable Long id, @RequestBody SkillRequestDTO requestDTO) {
        SkillResponseDTO updated = skillService.update(id, requestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar habilidad", description = "Elimina una habilidad por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = skillService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar por estado", description = "Obtiene habilidades por estado")
    public ResponseEntity<List<SkillResponseDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(skillService.findByStatus(status));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrar por categoria", description = "Obtiene habilidades por categoria")
    public ResponseEntity<List<SkillResponseDTO>> findByCategory(@PathVariable String category) {
        return ResponseEntity.ok(skillService.findByCategory(category));
    }
}
