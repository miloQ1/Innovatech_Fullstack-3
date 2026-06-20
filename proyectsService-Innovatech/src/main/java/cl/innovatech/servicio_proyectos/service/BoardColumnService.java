package cl.innovatech.servicio_proyectos.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.servicio_proyectos.model.BoardColumn;
import cl.innovatech.servicio_proyectos.model.Phase;
import cl.innovatech.servicio_proyectos.repository.BoardColumnRepository;
import cl.innovatech.servicio_proyectos.repository.PhaseRepository;

@Service
public class BoardColumnService {

    private final BoardColumnRepository columnRepository;
    private final PhaseRepository phaseRepository;

    public BoardColumnService(BoardColumnRepository columnRepository,
                              PhaseRepository phaseRepository) {
        this.columnRepository = columnRepository;
        this.phaseRepository = phaseRepository;
    }

    public List<BoardColumn> getColumns(Long phaseId) {
        return columnRepository.findByPhasePhaseIdOrderBySequenceOrderAsc(phaseId);
    }

    public BoardColumn createColumn(Long phaseId, BoardColumn column) {
        Phase phase = phaseRepository.findById(phaseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase no encontrada"));
        column.setPhase(phase);
        if (column.getSequenceOrder() == null) {
            int count = columnRepository.findByPhasePhaseIdOrderBySequenceOrderAsc(phaseId).size();
            column.setSequenceOrder(count + 1);
        }
        if (column.getColor() == null || column.getColor().isBlank()) {
            column.setColor("#6366f1");
        }
        return columnRepository.save(column);
    }

    public BoardColumn updateColumn(Long columnId, Map<String, Object> updates) {
        BoardColumn col = columnRepository.findById(columnId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Columna no encontrada"));
        if (updates.containsKey("name")) col.setName((String) updates.get("name"));
        if (updates.containsKey("color")) col.setColor((String) updates.get("color"));
        if (updates.containsKey("sequenceOrder"))
            col.setSequenceOrder((Integer) updates.get("sequenceOrder"));
        if (updates.containsKey("mappedStatus"))
            col.setMappedStatus((String) updates.get("mappedStatus"));
        return columnRepository.save(col);
    }

    public void deleteColumn(Long columnId) {
        if (!columnRepository.existsById(columnId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Columna no encontrada");
        columnRepository.deleteById(columnId);
    }

    // Columnas por defecto al crear una fase
    public void createDefaultColumns(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase no encontrada"));

        String[][] defaults = {
            {"To Do",      "#94a3b8", "TODO"},
            {"In Progress","#4f46e5", "IN_PROGRESS"},
            {"In Review",  "#d97706", "IN_REVIEW"},
            {"Done",       "#059669", "DONE"},
        };

        for (int i = 0; i < defaults.length; i++) {
            BoardColumn col = new BoardColumn();
            col.setPhase(phase);
            col.setName(defaults[i][0]);
            col.setColor(defaults[i][1]);
            col.setSequenceOrder(i + 1);
            col.setMappedStatus(defaults[i][2]);
            columnRepository.save(col);
        }
    }
}