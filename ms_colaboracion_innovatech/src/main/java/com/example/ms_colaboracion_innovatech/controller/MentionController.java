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
import com.example.ms_colaboracion_innovatech.dto.MentionRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.MentionResponseDTO;
import com.example.ms_colaboracion_innovatech.service.MentionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/mentions")
@Tag(name = "Mentions", description = "Mention management")
public class MentionController {

    private final MentionService mentionService;

    public MentionController(MentionService mentionService) {
        this.mentionService = mentionService;
    }

    @GetMapping
    @Operation(summary = "Get all mentions")
    public ResponseEntity<List<MentionResponseDTO>> getAll() {
        return ResponseEntity.ok(mentionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mention by id")
    public ResponseEntity<MentionResponseDTO> getById(@PathVariable Long id) {
        MentionResponseDTO response = mentionService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create mention")
    public ResponseEntity<MentionResponseDTO> create(@RequestBody MentionRequestDTO requestDTO) {
        MentionResponseDTO response = mentionService.save(requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mention")
    public ResponseEntity<MentionResponseDTO> update(@PathVariable Long id,
            @RequestBody MentionRequestDTO requestDTO) {
        MentionResponseDTO response = mentionService.update(id, requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mention")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = mentionService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Get mentions by resource")
    public ResponseEntity<List<MentionResponseDTO>> getByResourceId(@PathVariable Long resourceId) {
        return ResponseEntity.ok(mentionService.findByResourceId(resourceId));
    }
}
