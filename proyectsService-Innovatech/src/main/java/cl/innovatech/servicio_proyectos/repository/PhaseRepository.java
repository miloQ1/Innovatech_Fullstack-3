package cl.innovatech.servicio_proyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.innovatech.servicio_proyectos.model.Phase;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {

    List<Phase> findByProjectProjectIdOrderBySequenceOrderAsc(Long projectId);
}
