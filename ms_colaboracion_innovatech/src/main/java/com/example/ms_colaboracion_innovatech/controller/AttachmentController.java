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
import com.example.ms_colaboracion_innovatech.dto.AttachmentRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.AttachmentResponseDTO;
import com.example.ms_colaboracion_innovatech.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/attachments")
@Tag(name = "Attachments", description = "Attachment management")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    @Operation(summary = "Get all attachments")
    public ResponseEntity<List<AttachmentResponseDTO>> getAll() {
        return ResponseEntity.ok(attachmentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attachment by id")
    public ResponseEntity<AttachmentResponseDTO> getById(@PathVariable Long id) {
        AttachmentResponseDTO response = attachmentService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create attachment")
    public ResponseEntity<AttachmentResponseDTO> create(@RequestBody AttachmentRequestDTO requestDTO) {
        AttachmentResponseDTO response = attachmentService.save(requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update attachment")
    public ResponseEntity<AttachmentResponseDTO> update(@PathVariable Long id,
            @RequestBody AttachmentRequestDTO requestDTO) {
        AttachmentResponseDTO response = attachmentService.update(id, requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attachment")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = attachmentService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comment/{commentId}")
    @Operation(summary = "Get attachments by comment")
    public ResponseEntity<List<AttachmentResponseDTO>> getByCommentId(@PathVariable Long commentId) {
        return ResponseEntity.ok(attachmentService.findByCommentId(commentId));
    }
}
