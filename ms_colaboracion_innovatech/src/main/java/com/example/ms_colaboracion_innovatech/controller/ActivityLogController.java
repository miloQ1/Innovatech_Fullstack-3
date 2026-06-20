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
import com.example.ms_colaboracion_innovatech.dto.ActivityLogRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.ActivityLogResponseDTO;
import com.example.ms_colaboracion_innovatech.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/activity-logs")
@Tag(name = "Activity Logs", description = "Activity log management")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    @Operation(summary = "Get all activity logs")
    public ResponseEntity<List<ActivityLogResponseDTO>> getAll() {
        return ResponseEntity.ok(activityLogService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity log by id")
    public ResponseEntity<ActivityLogResponseDTO> getById(@PathVariable Long id) {
        ActivityLogResponseDTO response = activityLogService.findById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create activity log")
    public ResponseEntity<ActivityLogResponseDTO> create(@RequestBody ActivityLogRequestDTO requestDTO) {
        ActivityLogResponseDTO response = activityLogService.save(requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update activity log")
    public ResponseEntity<ActivityLogResponseDTO> update(@PathVariable Long id,
            @RequestBody ActivityLogRequestDTO requestDTO) {
        ActivityLogResponseDTO response = activityLogService.update(id, requestDTO);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete activity log")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = activityLogService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get activity logs by project")
    public ResponseEntity<List<ActivityLogResponseDTO>> getByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(activityLogService.findByProjectId(projectId));
    }
}
