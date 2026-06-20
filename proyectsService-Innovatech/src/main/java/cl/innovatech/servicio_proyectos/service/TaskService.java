package cl.innovatech.servicio_proyectos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.innovatech.servicio_proyectos.factory.TaskStatusMessage;
import cl.innovatech.servicio_proyectos.factory.TaskStatusMessageFactory;
import cl.innovatech.servicio_proyectos.model.Phase;
import cl.innovatech.servicio_proyectos.model.Project;
import cl.innovatech.servicio_proyectos.model.Task;
import cl.innovatech.servicio_proyectos.model.enums.TaskStatus;
import cl.innovatech.servicio_proyectos.repository.PhaseRepository;
import cl.innovatech.servicio_proyectos.repository.ProjectRepository;
import cl.innovatech.servicio_proyectos.repository.TaskRepository;
import cl.innovatech.servicio_proyectos.util.UserContext;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final PhaseRepository phaseRepository;
    private final TaskStatusMessageFactory taskStatusMessageFactory;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       PhaseRepository phaseRepository,
                       TaskStatusMessageFactory taskStatusMessageFactory) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.phaseRepository = phaseRepository;
        this.taskStatusMessageFactory = taskStatusMessageFactory;
    }

    public Task createTask(Long projectId, Task task) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        task.setCreatedBy(UserContext.getCurrentUserId());
        task.setProject(project);

        if (task.getInputPhaseId() != null) {
            Phase phase = phaseRepository.findById(task.getInputPhaseId())
                .orElseThrow(() -> new RuntimeException("Fase no encontrada"));
            task.setPhase(phase);
        }

        if (task.getStatus() == null) task.setStatus(TaskStatus.TODO);

        long count = taskRepository.countByProjectProjectId(projectId);
        String code = project.getCode().split("-")[0] + "-" + String.format("%03d", count + 1);
        task.setTaskCode(code);

        TaskStatusMessage message = taskStatusMessageFactory
            .createMessage(task.getStatus().name(), task.getTitle());
        System.out.println("=== [TaskFactory] Tarea creada: " + message);

        return taskRepository.save(task);
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectProjectIdWithPhase(projectId);
    }

    public List<Task> getTasksByPhase(Long phaseId) {
        return taskRepository.findByPhasePhaseId(phaseId);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
    }

    public Task updateTask(Long id, Task task) {
        Task existente = getTaskById(id);

        if (task.getStatus() != null && !existente.getStatus().equals(task.getStatus())) {
            TaskStatusMessage message = taskStatusMessageFactory
                .createMessage(task.getStatus().name(), existente.getTitle());
            System.out.println("=== [TaskFactory] Status cambiado: " + message);
            System.out.println("=== [TaskFactory] " + existente.getStatus() + " → " + task.getStatus());
        }

        existente.setTitle(task.getTitle());
        existente.setDescription(task.getDescription());
        existente.setPriority(task.getPriority());
        existente.setStatus(task.getStatus());
        existente.setAssignedResourceId(task.getAssignedResourceId());
        existente.setEstimatedHours(task.getEstimatedHours());
        existente.setActualHours(task.getActualHours());
        existente.setStartDate(task.getStartDate());
        existente.setDueDate(task.getDueDate());
        existente.setUpdatedBy(UserContext.getCurrentUserId());

        return taskRepository.save(existente);
    }

    public void deleteTask(Long id) {
        Task existente = getTaskById(id);

        TaskStatusMessage message = taskStatusMessageFactory
            .createMessage("CANCELLED", existente.getTitle());
        System.out.println("=== [TaskFactory] Tarea cancelada: " + message);

        existente.setStatus(TaskStatus.CANCELLED);
        taskRepository.save(existente);
    }
}