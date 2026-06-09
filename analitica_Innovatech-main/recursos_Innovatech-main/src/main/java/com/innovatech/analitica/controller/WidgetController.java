package com.innovatech.analitica.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innovatech.analitica.model.Widget;
import com.innovatech.analitica.service.WidgetService;

@RestController
@RequestMapping("/api/widgets")
public class WidgetController {

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @PostMapping
    public ResponseEntity<Widget> createWidget(@RequestBody Widget widget) {
        return ResponseEntity.status(HttpStatus.CREATED).body(widgetService.createWidget(widget));
    }

    @GetMapping
    public ResponseEntity<List<Widget>> getAllWidgets() {
        return ResponseEntity.ok(widgetService.getAllWidgets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Widget> getWidgetById(@PathVariable Long id) {
        return ResponseEntity.ok(widgetService.getWidgetById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Widget> updateWidget(@PathVariable Long id,@RequestBody Widget widget) {
        return ResponseEntity.ok(widgetService.updateWidget(id, widget));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWidget(@PathVariable Long id) {
        widgetService.deleteWidget(id);
        return ResponseEntity.noContent().build();
    }
}
