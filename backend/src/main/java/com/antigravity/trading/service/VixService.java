package com.antigravity.trading.service;

import org.springframework.stereotype.Service;

@Service
public class VixService {

    // Mocking real India VIX behavior
    // In a real scenario, this would poll an API or fetch from Angel One
    private double currentVix = 14.2;

    public double getCurrentVix() {
        // Adding slight random drift to simulate live changes
        currentVix += (Math.random() - 0.5) * 0.1;
        return Math.round(currentVix * 100.0) / 100.0;
    }

    public String getVixLevel(double vix) {
        if (vix < 13) return "LOW IV";
        if (vix <= 20) return "MODERATE";
        if (vix <= 25) return "HIGH";
        return "EXTREME FEAR";
    }
}
