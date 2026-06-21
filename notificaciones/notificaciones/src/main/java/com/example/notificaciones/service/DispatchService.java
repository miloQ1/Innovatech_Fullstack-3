package com.example.notificaciones.service;

import com.example.notificaciones.model.Dispatch;
import com.example.notificaciones.model.NotificationEvent;
import com.example.notificaciones.repository.DispatchRepository;
import com.example.notificaciones.repository.NotificationEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final NotificationEventRepository notificationEventRepository;

    public DispatchService(DispatchRepository dispatchRepository,
                           NotificationEventRepository notificationEventRepository) {
        this.dispatchRepository = dispatchRepository;
        this.notificationEventRepository = notificationEventRepository;
    }

    public Dispatch createDispatch(Long eventId, Dispatch dispatch) {
        NotificationEvent notificationEvent = notificationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + eventId));
        dispatch.setNotificationEvent(notificationEvent);
        return dispatchRepository.save(dispatch);
    }

    public List<Dispatch> getAllDispatches() {
        return dispatchRepository.findAll();
    }

    public Dispatch getDispatchById(Long id) {
        return dispatchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispatch no encontrado con id: " + id));
    }

    public List<Dispatch> getDispatchesByEvent(Long eventId) {
        return dispatchRepository.findByNotificationEventEventId(eventId);
    }

    public List<Dispatch> getDispatchesByRecipient(Long recipientResourceId) {
        return dispatchRepository.findByRecipientResourceId(recipientResourceId);
    }

    public Dispatch updateDispatch(Long id, Dispatch dispatch) {
        Dispatch existente = getDispatchById(id);
        existente.setRecipientResourceId(dispatch.getRecipientResourceId());
        existente.setChannel(dispatch.getChannel());
        existente.setDeliveryStatus(dispatch.getDeliveryStatus());
        existente.setRetryCount(dispatch.getRetryCount());
        existente.setRenderedSubject(dispatch.getRenderedSubject());
        existente.setRenderedBody(dispatch.getRenderedBody());
        existente.setSentAt(dispatch.getSentAt());
        existente.setErrorMessage(dispatch.getErrorMessage());
        return dispatchRepository.save(existente);
    }

    public void deleteDispatch(Long id) {
        Dispatch existente = getDispatchById(id);
        dispatchRepository.delete(existente);
    }
}
