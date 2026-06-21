package com.example.notificaciones.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TemplateRendererService {

    public String render(String template, Map<String, Object> payload) {
        if (template == null) {
            return null;
        }
        String rendered = template;
        if (payload == null || payload.isEmpty()) {
            return rendered;
        }
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
            rendered = rendered.replace(key, value);
        }
        return rendered;
    }
}
