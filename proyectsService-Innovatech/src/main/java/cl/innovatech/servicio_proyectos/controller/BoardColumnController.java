package cl.innovatech.servicio_proyectos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.innovatech.servicio_proyectos.model.BoardColumn;
import cl.innovatech.servicio_proyectos.service.BoardColumnService;

@RestController
@RequestMapping("/api/phases/{phaseId}/columns")
public class BoardColumnController {

    private final BoardColumnService columnService;

    public BoardColumnController(BoardColumnService columnService) {
        this.columnService = columnService;
    }

    @GetMapping
    public ResponseEntity<List<BoardColumn>> getColumns(@PathVariable Long phaseId) {
        return ResponseEntity.ok(columnService.getColumns(phaseId));
    }

    @PostMapping
    public ResponseEntity<BoardColumn> createColumn(
            @PathVariable Long phaseId,
            @RequestBody BoardColumn column) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(columnService.createColumn(phaseId, column));
    }

    @PostMapping("/defaults")
    public ResponseEntity<Void> createDefaults(@PathVariable Long phaseId) {
        columnService.createDefaultColumns(phaseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{columnId}")
    public ResponseEntity<BoardColumn> updateColumn(
            @PathVariable Long phaseId,
            @PathVariable Long columnId,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(columnService.updateColumn(columnId, updates));
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(
            @PathVariable Long phaseId,
            @PathVariable Long columnId) {
        columnService.deleteColumn(columnId);
        return ResponseEntity.noContent().build();
    }
}
