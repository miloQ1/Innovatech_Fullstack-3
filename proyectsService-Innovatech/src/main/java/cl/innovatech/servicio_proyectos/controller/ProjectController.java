package cl.innovatech.servicio_proyectos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.innovatech.servicio_proyectos.model.Project;
import cl.innovatech.servicio_proyectos.model.enums.ProjectStatus;
import cl.innovatech.servicio_proyectos.service.ProjectService;
import cl.innovatech.servicio_proyectos.util.UserContext;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/client/{clientId}")
public ResponseEntity<Project> createProject(
        @PathVariable Long clientId,
        @RequestBody Project project,
        @RequestHeader(value = "X-User-Name", required = false) String userName) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(projectService.createProject(clientId, project, userName));
}

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Project>> getProjectsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(projectService.getProjectsByClient(clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
public ResponseEntity<Project> updateStatus(
        @PathVariable Long id,
        @RequestBody java.util.Map<String, String> body) {
    Project existente = projectService.getProjectById(id);
    existente.setStatus(ProjectStatus.valueOf(body.get("status")));
    existente.setUpdatedBy(UserContext.getCurrentUserId());
    return ResponseEntity.ok(projectService.saveProject(existente));
}
}
