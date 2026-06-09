package com.example.notificaciones.service;

import com.example.notificaciones.model.Preference;
import com.example.notificaciones.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;

    public PreferenceService(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public Preference createPreference(Preference preference) {
        return preferenceRepository.save(preference);
    }

    public List<Preference> getAllPreferences() {
        return preferenceRepository.findAll();
    }

    public Preference getPreferenceById(Long id) {
        return preferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preferencia no encontrada con id: " + id));
    }

    public List<Preference> getPreferencesByResource(Long resourceId) {
        return preferenceRepository.findByResourceId(resourceId);
    }

    public Preference updatePreference(Long id, Preference preference) {
        Preference existente = getPreferenceById(id);
        existente.setResourceId(preference.getResourceId());
        existente.setChannel(preference.getChannel());
        existente.setEnabled(preference.getEnabled());
        existente.setFrequency(preference.getFrequency());
        return preferenceRepository.save(existente);
    }

    public void deletePreference(Long id) {
        Preference existente = getPreferenceById(id);
        preferenceRepository.delete(existente);
    }
}
