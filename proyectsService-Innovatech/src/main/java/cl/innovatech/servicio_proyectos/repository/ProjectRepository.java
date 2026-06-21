package cl.innovatech.servicio_proyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.innovatech.servicio_proyectos.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClientId(Long clientId);
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.userId = :userId")
    List<Project> findByMemberUserId(@Param("userId") String userId);
}
