package cl.innovatech.servicio_proyectos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.innovatech.servicio_proyectos.model.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject_ProjectId(Long projectId);
    Optional<ProjectMember> findByProject_ProjectIdAndUserId(Long projectId, String userId);
    boolean existsByProject_ProjectIdAndUserId(Long projectId, String userId);
}