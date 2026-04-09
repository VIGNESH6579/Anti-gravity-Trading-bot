package com.antigravity.trading.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(60 * 1000L * 60); // 1 hour timeout
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(emitter);
        });

        return emitter;
    }

    public void sendPushNotification(String title, String message) {
        String escapedMessage = message.replace("\"", "\\\"").replace("\n", "\\n");
        String payload = String.format("{\"title\":\"%s\", \"message\":\"%s\"}", title, escapedMessage);
        List<SseEmitter> deadEmitters = new ArrayList<>();
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("push-notification").data(payload));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        
        emitters.removeAll(deadEmitters);
    }
}
