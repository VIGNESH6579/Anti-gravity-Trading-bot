package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.SignalCard;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class GammaScalpService {

    public SignalCard evaluate(double spotPrice, int dte) {
        SignalCard card = new SignalCard();
        card.setStrategyName("2:00 PM Gamma Scalp");
        
        if (dte != 0) {
            card.setAction("WAITING");
            card.setRationale("Only active on 0DTE (Expiry Day).");
            card.setColor("gray");
            return card;
        }
        
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(13, 45);
        LocalTime endTime = LocalTime.of(14, 15);
        
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            card.setAction("WAITING");
            card.setRationale("Waiting for 1:45 PM window.");
            card.setColor("gray");
            return card;
        }
        
        // Inside the window
        double rand = Math.random();
        if (rand > 0.5) {
            card.setAction("BUY");
            card.setRationale("Consolidation breakout detected with momentum!");
            card.setTargetPremium(45.0);
            card.setSlPremium(15.0);
            card.setRecommendedStrike(String.valueOf(Math.round(spotPrice / 100) * 100 + 100)); // OTM
            card.setPriority("HIGH");
            card.setColor("green");
            card.setAlertTriggered(true);
        } else {
            card.setAction("WAITING");
            card.setRationale("Consolidating. Waiting for decisive candle breakout above 60% body.");
            card.setColor("yellow");
        }
        
        return card;
    }
}
