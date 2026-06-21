package com.example.notificaciones.notifier;

import com.example.notificaciones.model.Dispatch;
import com.example.notificaciones.model.enums.NotificationChannel;

public interface Notifier {
    NotificationChannel getChannel();
    Dispatch send(Dispatch dispatch);
}
