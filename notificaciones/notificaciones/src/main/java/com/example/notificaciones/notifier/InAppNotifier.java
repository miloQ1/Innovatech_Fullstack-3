package com.example.notificaciones.notifier;

import com.example.notificaciones.model.Dispatch;
import com.example.notificaciones.model.enums.DeliveryStatus;
import com.example.notificaciones.model.enums.NotificationChannel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InAppNotifier implements Notifier {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    public Dispatch send(Dispatch dispatch) {
        // Simulación académica: la notificación queda disponible para ser listada en la app.
        dispatch.setDeliveryStatus(DeliveryStatus.SENT);
        dispatch.setSentAt(LocalDateTime.now());
        dispatch.setErrorMessage(null);
        return dispatch;
    }
}
