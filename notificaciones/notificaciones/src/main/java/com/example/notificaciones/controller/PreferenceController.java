package com.example.notificaciones.controller;

import com.example.notificaciones.model.Preference;
import com.example.notificaciones.service.PreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping
    public ResponseEntity<Preference> createPreference(@RequestBody Preference preference) {
        return ResponseEntity.status(HttpStatus.CREATED).body(preferenceService.createPreference(preference));
    }

    @GetMapping
    public ResponseEntity<List<Preference>> getAllPreferences() {
        return ResponseEntity.ok(preferenceService.getAllPreferences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Preference> getPreferenceById(@PathVariable Long id) {
        return ResponseEntity.ok(preferenceService.getPreferenceById(id));
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<Preference>> getPreferencesByResource(@PathVariable Long resourceId) {
        return ResponseEntity.ok(preferenceService.getPreferencesByResource(resourceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Preference> updatePreference(@PathVariable Long id,
                                                       @RequestBody Preference preference) {
        return ResponseEntity.ok(preferenceService.updatePreference(id, preference));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }
}
