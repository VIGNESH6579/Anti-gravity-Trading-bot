package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.SignalCard;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ORBService {

    public SignalCard evaluate(double spotPrice, String sessionType, double vix) {
        SignalCard card = new SignalCard();
        card.setStrategyName("Opening Range Breakout (ORB)");
        
        LocalTime now = LocalTime.now(java.time.ZoneId.of("Asia/Kolkata"));
        LocalTime start = LocalTime.of(9, 15);
        LocalTime end = LocalTime.of(10, 15);
        
        if (now.isBefore(start) || now.isAfter(end)) {
            card.setAction("WAITING");
            card.setRationale("Outside ORB window (9:15-10:15 IST).");
            card.setColor("gray");
            return card;
        }
        
        if (vix > 18) {
            card.setAction("BLOCKED");
            card.setRationale("VIX > 18. High volatility ORBs untrustworthy.");
            card.setColor("gray");
            return card;
        }
        
        if ("SIDEWAYS".equals(sessionType)) {
            card.setAction("BLOCKED");
            card.setRationale("Sideways session classified. Fading breakouts.");
            card.setColor("gray");
            return card;
        }

        card.setAction("BUY");
        card.setRationale("Valid Range formed. Waiting for breakout confirmation with 1.5x Volume.");
        card.setColor("yellow");
        
        return card;
    }
}
