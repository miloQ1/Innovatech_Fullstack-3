package com.example.notificaciones.controller;

import com.example.notificaciones.model.Preference;
import com.example.notificaciones.service.CurrentUserResourceService;
import com.example.notificaciones.service.PreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final CurrentUserResourceService currentUserResourceService;

    public PreferenceController(PreferenceService preferenceService,
                                CurrentUserResourceService currentUserResourceService) {
        this.preferenceService = preferenceService;
        this.currentUserResourceService = currentUserResourceService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<Preference>> getMyPreferences(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                             @RequestHeader(value = "X-User-Email", required = false) String email) {
        Long resourceId = currentUserResourceService.resolveCurrentResourceId(userId, email);
        return ResponseEntity.ok(preferenceService.getPreferencesByResource(resourceId));
    }

    @PostMapping("/me")
    public ResponseEntity<Preference> saveMyPreference(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                       @RequestHeader(value = "X-User-Email", required = false) String email,
                                                       @RequestBody Preference preference) {
        Long resourceId = currentUserResourceService.resolveCurrentResourceId(userId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(preferenceService.savePreferenceForResource(resourceId, preference));
    }

    @PostMapping
    public ResponseEntity<Preference> createPreference(@RequestHeader(value = "X-User-Role", required = false) String role,
                                                       @RequestHeader(value = "X-User-Id", required = false) String userId,
                                                       @RequestHeader(value = "X-User-Email", required = false) String email,
                                                       @RequestBody Preference preference) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            currentUserResourceService.assertCurrentUserOwnsResource(userId, email, preference.getResourceId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(preferenceService.createPreference(preference));
    }

    @GetMapping
    public ResponseEntity<List<Preference>> getAllPreferences(@RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        return ResponseEntity.ok(preferenceService.getAllPreferences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Preference> getPreferenceById(@PathVariable Long id,
                                                        @RequestHeader(value = "X-User-Role", required = false) String role,
                                                        @RequestHeader(value = "X-User-Id", required = false) String userId,
                                                        @RequestHeader(value = "X-User-Email", required = false) String email) {
        Preference preference = preferenceService.getPreferenceById(id);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            currentUserResourceService.assertCurrentUserOwnsResource(userId, email, preference.getResourceId());
        }
        return ResponseEntity.ok(preference);
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<Preference>> getPreferencesByResource(@PathVariable Long resourceId,
                                                                     @RequestHeader(value = "X-User-Role", required = false) String role,
                                                                     @RequestHeader(value = "X-User-Id", required = false) String userId,
                                                                     @RequestHeader(value = "X-User-Email", required = false) String email) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            currentUserResourceService.assertCurrentUserOwnsResource(userId, email, resourceId);
        }
        return ResponseEntity.ok(preferenceService.getPreferencesByResource(resourceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Preference> updatePreference(@PathVariable Long id,
                                                       @RequestHeader(value = "X-User-Role", required = false) String role,
                                                       @RequestHeader(value = "X-User-Id", required = false) String userId,
                                                       @RequestHeader(value = "X-User-Email", required = false) String email,
                                                       @RequestBody Preference preference) {
        Preference current = preferenceService.getPreferenceById(id);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            currentUserResourceService.assertCurrentUserOwnsResource(userId, email, current.getResourceId());
            preference.setResourceId(current.getResourceId());
        }
        return ResponseEntity.ok(preferenceService.updatePreference(id, preference));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id,
                                                 @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }

    private void assertAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere rol ADMIN");
        }
    }
}
