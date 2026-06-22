package com.example.notificaciones.service;

import com.example.notificaciones.model.Preference;
import com.example.notificaciones.model.enums.NotificationChannel;
import com.example.notificaciones.model.enums.NotificationFrequency;
import com.example.notificaciones.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
        ensureDefaultPreferences(resourceId);
        return preferenceRepository.findByResourceId(resourceId);
    }

    public Preference savePreferenceForResource(Long resourceId, Preference preference) {
        NotificationChannel channel = preference.getChannel() == null ? NotificationChannel.IN_APP : preference.getChannel();
        Preference existente = preferenceRepository.findByResourceIdAndChannel(resourceId, channel)
                .orElseGet(() -> {
                    Preference created = new Preference();
                    created.setResourceId(resourceId);
                    created.setChannel(channel);
                    return created;
                });

        existente.setResourceId(resourceId);
        existente.setChannel(channel);
        existente.setEnabled(preference.getEnabled() == null ? Boolean.TRUE : preference.getEnabled());
        existente.setFrequency(preference.getFrequency() == null ? NotificationFrequency.IMMEDIATE : preference.getFrequency());
        return preferenceRepository.save(existente);
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

    public void ensureDefaultPreferences(Long resourceId) {
        if (resourceId == null) {
            return;
        }
        Arrays.stream(NotificationChannel.values()).forEach(channel -> {
            if (preferenceRepository.findByResourceIdAndChannel(resourceId, channel).isEmpty()) {
                Preference preference = new Preference();
                preference.setResourceId(resourceId);
                preference.setChannel(channel);
                preference.setEnabled(Boolean.TRUE);
                preference.setFrequency(NotificationFrequency.IMMEDIATE);
                preferenceRepository.save(preference);
            }
        });
    }
}
