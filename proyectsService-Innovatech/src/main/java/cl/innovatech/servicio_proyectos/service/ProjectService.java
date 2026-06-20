package cl.innovatech.servicio_proyectos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.servicio_proyectos.model.Client;
import cl.innovatech.servicio_proyectos.model.Project;
import cl.innovatech.servicio_proyectos.model.ProjectMember;
import cl.innovatech.servicio_proyectos.model.enums.ProjectStatus;
import cl.innovatech.servicio_proyectos.repository.ClientRepository;
import cl.innovatech.servicio_proyectos.repository.ProjectMemberRepository;
import cl.innovatech.servicio_proyectos.repository.ProjectRepository;
import cl.innovatech.servicio_proyectos.util.UserContext;

@Service
public class ProjectService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    public ProjectService(ProjectRepository projectRepository,
                      ClientRepository clientRepository,
                      ProjectMemberRepository projectMemberRepository) {
    this.projectRepository = projectRepository;
    this.clientRepository = clientRepository;
    this.projectMemberRepository = projectMemberRepository;
}

    public Project createProject(Long clientId, Project project, String userName) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        project.setClient(client);
        project.setCreatedBy(UserContext.getCurrentUserId());
        Project saved = projectRepository.save(project);

        // ← Auto-agregar creador como miembro
        String userId = UserContext.getCurrentUserId();
        if (userId != null) {
            ProjectMember member = new ProjectMember();
            member.setProject(saved);
            member.setUserId(userId);
            member.setUserName(userName != null ? userName : "unknown");
            member.setRole("OWNER"); // ← agregar aquí // se actualiza cuando el front mande el userName
            projectMemberRepository.save(member);
        }

        return saved;
    }

    public List<Project> getAllProjects() {
    String userId = UserContext.getCurrentUserId();
    if (userId == null) return List.of();
    return projectRepository.findByMemberUserId(userId);
    }

    public List<Project> getProjectsByClient(Long clientId) {
    System.out.println("=== getProjectsByClient clientId: " + clientId);
    System.out.println("=== userId: " + UserContext.getCurrentUserId());
    
    String userId = UserContext.getCurrentUserId();
    List<Project> allProjects = projectRepository.findByClientClientId(clientId);
    System.out.println("=== proyectos encontrados: " + allProjects.size());
    
    if (userId == null) return List.of();
    
    return allProjects.stream()
        .filter(p -> {
            boolean isMember = projectMemberRepository
                .existsByProject_ProjectIdAndUserId(p.getProjectId(), userId);
            System.out.println("=== proyecto " + p.getProjectId() + " isMember: " + isMember);
            return isMember;
        })
        .collect(java.util.stream.Collectors.toList());
}

    public Project getProjectById(Long id) {
    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
    
    String userId = UserContext.getCurrentUserId();
    boolean isMember = projectMemberRepository
        .existsByProject_ProjectIdAndUserId(id, userId);
    
    if (!isMember) {
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.FORBIDDEN, 
            "No tienes acceso a este proyecto"
        );
    }
    
    return project;
}

    public Project updateProject(Long id, Project project) {
        Project existente = getProjectById(id);
        existente.setCode(project.getCode());
        existente.setName(project.getName());
        existente.setDescription(project.getDescription());
        existente.setStartDate(project.getStartDate());
        existente.setEndDate(project.getEndDate());
        existente.setBudget(project.getBudget());
        existente.setStatus(project.getStatus());
        existente.setProgressPct(project.getProgressPct());
        existente.setProjectManagerId(project.getProjectManagerId());
        existente.setUpdatedBy(UserContext.getCurrentUserId());
        return projectRepository.save(existente);
    }

    public void deleteProject(Long id) {
        Project existente = getProjectById(id);
        existente.setStatus(ProjectStatus.CANCELLED);
        projectRepository.save(existente);
    }

    public Project saveProject(Project project) {
    return projectRepository.save(project);
}
}
