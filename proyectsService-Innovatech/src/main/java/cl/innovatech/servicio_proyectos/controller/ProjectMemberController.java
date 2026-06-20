package cl.innovatech.servicio_proyectos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.innovatech.servicio_proyectos.model.ProjectMember;
import cl.innovatech.servicio_proyectos.service.ProjectMemberService;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService memberService;

    public ProjectMemberController(ProjectMemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectMember>> getMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(memberService.getMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<ProjectMember> addMember(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String userName = body.get("userName");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberService.addMember(projectId, userId, userName, firstName, lastName));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable String userId) {
        memberService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}