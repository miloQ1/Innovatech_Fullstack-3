package com.example.notificaciones.controller;

import com.example.notificaciones.model.Dispatch;
import com.example.notificaciones.service.DispatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatches")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Dispatch> createDispatch(@PathVariable Long eventId,
                                                   @RequestBody Dispatch dispatch) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dispatchService.createDispatch(eventId, dispatch));
    }

    @GetMapping
    public ResponseEntity<List<Dispatch>> getAllDispatches() {
        return ResponseEntity.ok(dispatchService.getAllDispatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispatch> getDispatchById(@PathVariable Long id) {
        return ResponseEntity.ok(dispatchService.getDispatchById(id));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Dispatch>> getDispatchesByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(dispatchService.getDispatchesByEvent(eventId));
    }

    @GetMapping("/recipient/{recipientResourceId}")
    public ResponseEntity<List<Dispatch>> getDispatchesByRecipient(@PathVariable Long recipientResourceId) {
        return ResponseEntity.ok(dispatchService.getDispatchesByRecipient(recipientResourceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispatch> updateDispatch(@PathVariable Long id,
                                                   @RequestBody Dispatch dispatch) {
        return ResponseEntity.ok(dispatchService.updateDispatch(id, dispatch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispatch(@PathVariable Long id) {
        dispatchService.deleteDispatch(id);
        return ResponseEntity.noContent().build();
    }
}
