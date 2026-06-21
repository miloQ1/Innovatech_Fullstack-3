package com.example.notificaciones.model;

import com.example.notificaciones.model.enums.DeliveryStatus;
import com.example.notificaciones.model.enums.NotificationChannel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "dispatches")
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispatch_id")
    private Long dispatchId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private NotificationEvent notificationEvent;

    @Column(name = "recipient_resource_id", nullable = false)
    private Long recipientResourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus;

    @Column(name = "rendered_subject", length = 255)
    private String renderedSubject;

    @Column(name = "rendered_body", columnDefinition = "TEXT")
    private String renderedBody;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (this.deliveryStatus == null) {
            this.deliveryStatus = DeliveryStatus.PENDING;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }

    public Long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Long dispatchId) {
        this.dispatchId = dispatchId;
    }

    public NotificationEvent getNotificationEvent() {
        return notificationEvent;
    }

    public void setNotificationEvent(NotificationEvent notificationEvent) {
        this.notificationEvent = notificationEvent;
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

    public String getRenderedSubject() {
        return renderedSubject;
    }

    public void setRenderedSubject(String renderedSubject) {
        this.renderedSubject = renderedSubject;
    }

    public String getRenderedBody() {
        return renderedBody;
    }

    public void setRenderedBody(String renderedBody) {
        this.renderedBody = renderedBody;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
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
