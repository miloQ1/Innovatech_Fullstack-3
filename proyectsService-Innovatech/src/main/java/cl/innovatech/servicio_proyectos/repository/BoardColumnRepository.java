package cl.innovatech.servicio_proyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.innovatech.servicio_proyectos.model.BoardColumn;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
    List<BoardColumn> findByPhasePhaseIdOrderBySequenceOrderAsc(Long phaseId);
    void deleteByPhasePhaseId(Long phaseId);
}