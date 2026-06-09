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

import com.innovatech.analitica.model.LayoutItem;
import com.innovatech.analitica.service.LayoutItemService;

@RestController
@RequestMapping("/api/layout-items")
public class LayoutItemController {

    private final LayoutItemService layoutItemService;

    public LayoutItemController(LayoutItemService layoutItemService) {
        this.layoutItemService = layoutItemService;
    }

    @PostMapping("/layout/{layoutId}/widget/{widgetId}")
    public ResponseEntity<LayoutItem> createLayoutItem(@PathVariable Long layoutId, @PathVariable Long widgetId, @RequestBody LayoutItem layoutItem) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(layoutItemService.createLayoutItem(layoutId, widgetId, layoutItem));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LayoutItem> getLayoutItemById(@PathVariable Long id) {
        return ResponseEntity.ok(layoutItemService.getLayoutItemById(id));
    }

    @GetMapping("/layout/{layoutId}")
    public ResponseEntity<List<LayoutItem>> getItemsByLayout(@PathVariable Long layoutId) {
        return ResponseEntity.ok(layoutItemService.getItemsByLayout(layoutId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LayoutItem> updateLayoutItem(@PathVariable Long id, @RequestBody LayoutItem layoutItem) {
        return ResponseEntity.ok(layoutItemService.updateLayoutItem(id, layoutItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLayoutItem(@PathVariable Long id) {
        layoutItemService.deleteLayoutItem(id);
        return ResponseEntity.noContent().build();
    }
}
