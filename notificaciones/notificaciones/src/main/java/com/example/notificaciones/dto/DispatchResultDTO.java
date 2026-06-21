package com.example.notificaciones.dto;

import com.example.notificaciones.model.enums.DeliveryStatus;
import com.example.notificaciones.model.enums.NotificationChannel;

import java.time.LocalDateTime;

public class DispatchResultDTO {

    private Long eventId;
    private Long dispatchId;
    private Long recipientResourceId;
    private NotificationChannel channel;
    private DeliveryStatus deliveryStatus;
    private String subject;
    private String body;
    private LocalDateTime sentAt;
    private String errorMessage;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Long dispatchId) {
        this.dispatchId = dispatchId;
    }

    public Long getRecipientResourceId() {
        return recipientResourceId;
    }

    public void setRecipientResourceId(Long recipientResourceId) {
        this.recipientResourceId = recipientResourceId;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
