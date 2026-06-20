package cl.innovatech.servicio_proyectos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.servicio_proyectos.model.Project;
import cl.innovatech.servicio_proyectos.model.ProjectMember;
import cl.innovatech.servicio_proyectos.repository.ProjectMemberRepository;
import cl.innovatech.servicio_proyectos.repository.ProjectRepository;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    public ProjectMemberService(ProjectMemberRepository memberRepository,
                                ProjectRepository projectRepository) {
        this.memberRepository = memberRepository;
        this.projectRepository = projectRepository;
    }

    public List<ProjectMember> getMembers(Long projectId) {
        return memberRepository.findByProject_ProjectId(projectId);
    }

    public ProjectMember addMember(Long projectId, String userId, String userName, String firstName, String lastName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

        if (memberRepository.existsByProject_ProjectIdAndUserId(projectId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya es miembro del proyecto");
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUserId(userId);
        member.setRole("MEMBER");
        member.setUserName(userName);
        member.setFirstName(firstName);
        member.setLastName(lastName);

        return memberRepository.save(member);
    }

    public void removeMember(Long projectId, String userId) {
        ProjectMember member = memberRepository
                .findByProject_ProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Miembro no encontrado"));

        memberRepository.delete(member);
    }
}
