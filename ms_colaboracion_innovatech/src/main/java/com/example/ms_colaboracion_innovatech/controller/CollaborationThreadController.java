package com.example.ms_colaboracion_innovatech.controller;

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
import com.example.ms_colaboracion_innovatech.dto.CollaborationThreadRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.CollaborationThreadResponseDTO;
import com.example.ms_colaboracion_innovatech.service.CollaborationThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/threads")
@Tag(name = "Collaboration Threads", description = "Thread management")
public class CollaborationThreadController {

    private final CollaborationThreadService threadService;

    public CollaborationThreadController(CollaborationThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping
    @Operation(summary = "Get all threads")
    public ResponseEntity<List<CollaborationThreadResponseDTO>> getAll() {
        return ResponseEntity.ok(threadService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get thread by id")
    public ResponseEntity<CollaborationThreadResponseDTO> getById(@PathVariable Long id) {
        CollaborationThreadResponseDTO response = threadService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create thread")
    public ResponseEntity<CollaborationThreadResponseDTO> create(
            @RequestBody CollaborationThreadRequestDTO requestDTO) {
        CollaborationThreadResponseDTO response = threadService.save(requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update thread")
    public ResponseEntity<CollaborationThreadResponseDTO> update(@PathVariable Long id,
            @RequestBody CollaborationThreadRequestDTO requestDTO) {
        CollaborationThreadResponseDTO response = threadService.update(id, requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete thread")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = threadService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get threads by project")
    public ResponseEntity<List<CollaborationThreadResponseDTO>> getByProjectId(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(threadService.findByProjectId(projectId));
    }
}
