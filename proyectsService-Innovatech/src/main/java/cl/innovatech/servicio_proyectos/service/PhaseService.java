package cl.innovatech.servicio_proyectos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.innovatech.servicio_proyectos.model.Phase;
import cl.innovatech.servicio_proyectos.model.Project;
import cl.innovatech.servicio_proyectos.model.Task;
import cl.innovatech.servicio_proyectos.model.enums.PhaseStatus;
import cl.innovatech.servicio_proyectos.repository.BoardColumnRepository;
import cl.innovatech.servicio_proyectos.repository.PhaseRepository;
import cl.innovatech.servicio_proyectos.repository.ProjectRepository;
import cl.innovatech.servicio_proyectos.repository.TaskRepository;
import cl.innovatech.servicio_proyectos.util.UserContext;

@Service
public class PhaseService {

    private final PhaseRepository phaseRepository;
    private final ProjectRepository projectRepository;

    private final TaskRepository taskRepository;
    private final BoardColumnService boardColumnService;

public PhaseService(PhaseRepository phaseRepository,
                    ProjectRepository projectRepository,
                    TaskRepository taskRepository,
                    BoardColumnService boardColumnService) {
    this.phaseRepository = phaseRepository;
    this.projectRepository = projectRepository;
    this.taskRepository = taskRepository;
    this.boardColumnService = boardColumnService;
}

    public Phase createPhase(Long projectId, Phase phase) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + projectId));
    phase.setProject(project);
    if (phase.getStatus() == null) {
        phase.setStatus(PhaseStatus.PENDING);
    }
    phase.setCreatedBy(UserContext.getCurrentUserId());
    Phase saved = phaseRepository.save(phase);
    // Crear columnas por defecto
    boardColumnService.createDefaultColumns(saved.getPhaseId());
    return saved;
}

    public Phase getPhaseById(Long id) {
        return phaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con id: " + id));
    }

    public Phase updatePhase(Long id, Phase phase) {
        Phase existente = getPhaseById(id);
        existente.setName(phase.getName());
        existente.setSequenceOrder(phase.getSequenceOrder());
        existente.setPlannedStart(phase.getPlannedStart());
        existente.setPlannedEnd(phase.getPlannedEnd());
        return phaseRepository.save(existente);
    }

    public void deletePhase(Long id) {
    Phase existente = getPhaseById(id);
    phaseRepository.delete(existente); // ← delete real en vez de soft delete
}

    public List<Phase> getPhasesByProject(Long projectId) {
    List<Phase> phases = phaseRepository.findByProjectProjectIdOrderBySequenceOrderAsc(projectId);
    phases.forEach(phase -> {
        List<Task> tasks = taskRepository.findByPhasePhaseId(phase.getPhaseId());
        phase.setStatus(computeStatus(tasks));
    });
    return phases;
}

private PhaseStatus computeStatus(List<Task> tasks) {
    if (tasks == null || tasks.isEmpty()) return PhaseStatus.PENDING;
    long done = tasks.stream()
        .filter(t -> t.getStatus().name().equals("DONE"))
        .count();
    if (done == tasks.size()) return PhaseStatus.COMPLETED;
    boolean anyActive = tasks.stream()
        .anyMatch(t -> t.getStatus().name().equals("IN_PROGRESS")
                    || t.getStatus().name().equals("IN_REVIEW"));
    return anyActive ? PhaseStatus.IN_PROGRESS : PhaseStatus.PENDING;
}
}
