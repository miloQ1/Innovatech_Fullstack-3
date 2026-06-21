package com.example.notificaciones.notifier;

import com.example.notificaciones.model.enums.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class NotifierFactory {

    private final Map<NotificationChannel, Notifier> notifiers = new EnumMap<>(NotificationChannel.class);

    public NotifierFactory(List<Notifier> notifierList) {
        for (Notifier notifier : notifierList) {
            notifiers.put(notifier.getChannel(), notifier);
        }
    }

    public Notifier getNotifier(NotificationChannel channel) {
        Notifier notifier = notifiers.get(channel);
        if (notifier == null) {
            throw new IllegalArgumentException("No existe un notificador para el canal: " + channel);
        }
        return notifier;
    }
}
