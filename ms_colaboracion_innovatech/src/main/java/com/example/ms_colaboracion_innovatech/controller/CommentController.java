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
import com.example.ms_colaboracion_innovatech.dto.CommentRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.CommentResponseDTO;
import com.example.ms_colaboracion_innovatech.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Comment management")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(summary = "Get all comments")
    public ResponseEntity<List<CommentResponseDTO>> getAll() {
        return ResponseEntity.ok(commentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by id")
    public ResponseEntity<CommentResponseDTO> getById(@PathVariable Long id) {
        CommentResponseDTO response = commentService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create comment")
    public ResponseEntity<CommentResponseDTO> create(@RequestBody CommentRequestDTO requestDTO) {
        CommentResponseDTO response = commentService.save(requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update comment")
    public ResponseEntity<CommentResponseDTO> update(@PathVariable Long id,
            @RequestBody CommentRequestDTO requestDTO) {
        CommentResponseDTO response = commentService.update(id, requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = commentService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/thread/{threadId}")
    @Operation(summary = "Get comments by thread")
    public ResponseEntity<List<CommentResponseDTO>> getByThreadId(@PathVariable Long threadId) {
        return ResponseEntity.ok(commentService.findByThreadId(threadId));
    }
}
