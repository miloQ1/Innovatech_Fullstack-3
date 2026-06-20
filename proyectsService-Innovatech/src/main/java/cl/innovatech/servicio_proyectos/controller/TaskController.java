package cl.innovatech.servicio_proyectos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.innovatech.servicio_proyectos.model.Task;
import cl.innovatech.servicio_proyectos.model.enums.TaskStatus;
import cl.innovatech.servicio_proyectos.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId, @RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, task));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @GetMapping("/phase/{phaseId}")
    public ResponseEntity<List<Task>> getTasksByPhase(@PathVariable Long phaseId) {
        return ResponseEntity.ok(taskService.getTasksByPhase(phaseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(   
        @PathVariable Long id,
        @RequestBody java.util.Map<String, String> body) {
    Task task = taskService.getTaskById(id);
    task.setStatus(TaskStatus.valueOf(body.get("status")));
    return ResponseEntity.ok(taskService.updateTask(id, task));
    }
}
