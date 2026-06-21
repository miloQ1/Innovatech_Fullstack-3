package com.example.notificaciones.notifier;

import com.example.notificaciones.model.Dispatch;
import com.example.notificaciones.model.enums.DeliveryStatus;
import com.example.notificaciones.model.enums.NotificationChannel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WebhookNotifier implements Notifier {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.WEBHOOK;
    }

    @Override
    public Dispatch send(Dispatch dispatch) {
        // Simulación académica: aquí se invocaría el endpoint configurado en webhook_subscriptions.
        dispatch.setDeliveryStatus(DeliveryStatus.SENT);
        dispatch.setSentAt(LocalDateTime.now());
        dispatch.setErrorMessage(null);
        return dispatch;
    }
}
