package com.antigravity.trading.service;

import org.springframework.stereotype.Service;
import java.time.LocalTime;

@Service
public class SessionClassifierService {
    
    // In a real application, this would calculate ATR from previous days.
    // Here we generate simulated classifications based on the current time of day.
    
    public String classifySession() {
        LocalTime now = LocalTime.now();
        
        // Simulating different session types based on simple rules or random logic for demonstration.
        // Let's pretend days that are even minutes are TRENDING, odd are SIDEWAYS (for testing UI).
        int currentMinute = now.getMinute();
        
        if (currentMinute % 2 == 0) {
            return "TRENDING";
        } else {
            return "SIDEWAYS";
        }
    }
}
