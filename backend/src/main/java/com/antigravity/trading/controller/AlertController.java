package com.antigravity.trading.controller;

import com.antigravity.trading.service.AlertDispatcherService;
import com.antigravity.trading.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
public class AlertController {

    @Autowired
    private AlertDispatcherService dispatcherService;
    
    @Autowired
    private SseService sseService;

    // --- SSE Endpoint for Browser Push Notifications ---
    @GetMapping("/sse/alerts")
    public SseEmitter streamAlerts() {
        return sseService.connect();
    }

    // --- Configuration Endpoints ---
    @PostMapping("/api/alerts/configure")
    public ResponseEntity<?> configureAlerts(@RequestBody Map<String, String> payload) {
        String ntfyTopic = payload.get("ntfyTopic");
        if (ntfyTopic != null && !ntfyTopic.trim().isEmpty()) {
            dispatcherService.setNtfyTopic(ntfyTopic.trim());
        }
        return ResponseEntity.ok().body(Map.of("message", "Alert preferences updated successfully"));
    }

    @PostMapping("/api/alerts/test")
    public ResponseEntity<?> testAlert(@RequestBody Map<String, String> payload) {
        String topic = payload.get("ntfyTopic");
        dispatcherService.sendTestAlert(topic);
        return ResponseEntity.ok().body(Map.of("message", "Test alert dispatched"));
    }
}
