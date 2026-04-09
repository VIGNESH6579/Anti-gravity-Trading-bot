package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.SignalCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AlertDispatcherService {

    @Autowired
    private SseService sseService;

    private String ntfyTopic = "Anti-Gravity-Trading-Bot";
    private final java.util.concurrent.ConcurrentHashMap<String, String> alertCache = new java.util.concurrent.ConcurrentHashMap<>();

    public void dispatch(SignalCard signal, String symbol) {
        if (!signal.isAlertTriggered()) {
            return;
        }
        
        String key = symbol + "_" + signal.getStrategyName();
        String action = signal.getAction();
        
        // Debouncer: Only send if action has changed (e.g. newly triggered BUY/SELL)
        if (action.equals(alertCache.get(key))) {
            return;
        }
        alertCache.put(key, action);

        String msg = String.format("🚨 SIGNAL 🚨\n%s \nAction: %s\nStrategy: %s",
                symbol, action, signal.getStrategyName());
        
        // 1. Browser Push (SSE)
        sseService.sendPushNotification("Anti-Gravity Alert", msg);

        // 2. ntfy.sh push notification (Free Android/iOS Push)
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("https://ntfy.sh/" + ntfyTopic, msg, String.class);
            System.out.println("✅ Sent ntfy.sh alert to topic: " + ntfyTopic);
        } catch (Exception e) {
            System.err.println("❌ Failed to send ntfy.sh alert: " + e.getMessage());
        }
    }
    
    public void sendTestAlert(String topic) {
        this.ntfyTopic = (topic != null && !topic.isEmpty()) ? topic : this.ntfyTopic;
        String msg = "✅ This is a TEST alert from Anti-Gravity Trading Bot!";
        
        sseService.sendPushNotification("Test Alert", msg);
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("https://ntfy.sh/" + this.ntfyTopic, msg, String.class);
        } catch (Exception e) {}
    }
    
    public void setNtfyTopic(String topic) {
        this.ntfyTopic = topic;
    }
}
