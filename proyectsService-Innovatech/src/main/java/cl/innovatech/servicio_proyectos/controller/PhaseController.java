package cl.innovatech.servicio_proyectos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.innovatech.servicio_proyectos.model.Phase;
import cl.innovatech.servicio_proyectos.service.PhaseService;

@RestController
@RequestMapping("/api/phases")
public class PhaseController {

    private final PhaseService phaseService;

    public PhaseController(PhaseService phaseService) {
        this.phaseService = phaseService;
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Phase> createPhase(@PathVariable Long projectId, @RequestBody Phase phase) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phaseService.createPhase(projectId, phase));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Phase>> getPhasesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(phaseService.getPhasesByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Phase> getPhaseById(@PathVariable Long id) {
        return ResponseEntity.ok(phaseService.getPhaseById(id));
    }

    @PatchMapping("/{id}")
public ResponseEntity<Phase> updatePhase(@PathVariable Long id, 
                                          @RequestBody java.util.Map<String, String> body) {
    Phase existente = phaseService.getPhaseById(id);
    if (body.containsKey("name")) existente.setName(body.get("name"));
    if (body.containsKey("plannedStart")) {
        existente.setPlannedStart(body.get("plannedStart") != null && !body.get("plannedStart").isBlank() 
            ? java.time.LocalDate.parse(body.get("plannedStart")) : null);
    }
    if (body.containsKey("plannedEnd")) {
        existente.setPlannedEnd(body.get("plannedEnd") != null && !body.get("plannedEnd").isBlank()
            ? java.time.LocalDate.parse(body.get("plannedEnd")) : null);
    }
    return ResponseEntity.ok(phaseService.updatePhase(id, existente));
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhase(@PathVariable Long id) {
        phaseService.deletePhase(id);
        return ResponseEntity.noContent().build();
    }
}
