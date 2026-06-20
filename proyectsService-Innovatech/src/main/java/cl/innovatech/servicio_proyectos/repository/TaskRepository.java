package cl.innovatech.servicio_proyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cl.innovatech.servicio_proyectos.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectProjectId(Long projectId);

    List<Task> findByPhasePhaseId(Long phaseId);
    
    @Query(
    "SELECT t FROM Task t LEFT JOIN FETCH t.phase WHERE t.project.projectId = :projectId"
    )
    List<Task> findByProjectProjectIdWithPhase(
        @org.springframework.data.repository.query.Param("projectId") Long projectId
    );

    long countByProjectProjectId(Long projectId);
}
