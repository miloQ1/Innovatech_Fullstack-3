package cl.innovatech.servicio_proyectos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.innovatech.servicio_proyectos.model.Project;
import cl.innovatech.servicio_proyectos.model.ProjectMember;
import cl.innovatech.servicio_proyectos.model.enums.ProjectStatus;
import cl.innovatech.servicio_proyectos.repository.ProjectMemberRepository;
import cl.innovatech.servicio_proyectos.repository.ProjectRepository;
import cl.innovatech.servicio_proyectos.util.UserContext;

@Service
public class ProjectService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final RestTemplate restTemplate;

    @Value("${ms-recursos.base-url:http://localhost:8083}")
    private String recursosBaseUrl;

    @Value("${ms-asignaciones.base-url:http://localhost:8091}")
    private String asignacionesBaseUrl;

    public ProjectService(ProjectRepository projectRepository,
                      ProjectMemberRepository projectMemberRepository,
                      RestTemplate restTemplate) {
    this.projectRepository = projectRepository;
    this.projectMemberRepository = projectMemberRepository;
    this.restTemplate = restTemplate;
}

    public Project createProject(Long clientId, Project project, String userName) {
        project.setClientId(clientId);
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

        autoAssignCreator(userId, saved);

        return saved;
    }

    // Vincula al creador del proyecto como recurso asignado, resolviendo su ficha
    // profesional (employeeCode == userId) y creando la asignación en ms-asignaciones.
    private void autoAssignCreator(String userId, Project project) {
        if (userId == null) {
            return;
        }
        try {
            HttpHeaders meHeaders = new HttpHeaders();
            meHeaders.set("X-User-Id", userId);
            Map<?, ?> professional = restTemplate.exchange(
                recursosBaseUrl + "/api/professionals/me",
                HttpMethod.GET,
                new HttpEntity<>(meHeaders),
                Map.class
            ).getBody();

            if (professional == null || professional.get("resourceId") == null) {
                return;
            }

            Map<String, Object> assignment = new HashMap<>();
            assignment.put("resourceId", ((Number) professional.get("resourceId")).longValue());
            assignment.put("projectId", project.getProjectId());
            assignment.put("projectRole", "Owner");
            assignment.put("allocationPct", 100);
            assignment.put("assignmentStatus", "ACTIVE");
            if (project.getStartDate() != null) {
                assignment.put("startDate", project.getStartDate().toString());
            }

            HttpHeaders writeHeaders = new HttpHeaders();
            writeHeaders.set("X-User-Role", "ADMIN");
            restTemplate.exchange(
                asignacionesBaseUrl + "/api/assignments",
                HttpMethod.POST,
                new HttpEntity<>(assignment, writeHeaders),
                Object.class
            );
        } catch (Exception e) {
            System.out.println("Warning: no se pudo auto-asignar al creador del proyecto: " + e.getMessage());
        }
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
    List<Project> allProjects = projectRepository.findByClientId(clientId);
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
