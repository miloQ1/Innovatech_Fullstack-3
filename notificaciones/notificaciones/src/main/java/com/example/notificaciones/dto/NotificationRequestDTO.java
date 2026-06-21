package com.example.notificaciones.dto;

import com.example.notificaciones.model.enums.NotificationChannel;

import java.util.List;
import java.util.Map;

public class NotificationRequestDTO {

    private String sourceService;
    private String eventType;
    private Long entityId;
    private List<Long> recipientResourceIds;
    private List<NotificationChannel> channels;
    private Map<String, Object> payload;

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<Long> getRecipientResourceIds() {
        return recipientResourceIds;
    }

    public void setRecipientResourceIds(List<Long> recipientResourceIds) {
        this.recipientResourceIds = recipientResourceIds;
    }

    public List<NotificationChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<NotificationChannel> channels) {
        this.channels = channels;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
