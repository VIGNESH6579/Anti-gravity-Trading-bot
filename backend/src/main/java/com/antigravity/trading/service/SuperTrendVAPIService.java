package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.SignalCard;
import org.springframework.stereotype.Service;

@Service
public class SuperTrendVAPIService {

    public SignalCard evaluate(double spotPrice, double vix) {
        SignalCard card = new SignalCard();
        card.setStrategyName("SuperTrend + VAPI + VWAP");
        
        if (vix > 25) {
            card.setAction("BLOCKED");
            card.setRationale("Blocked due to EXTREME FEAR (VIX > 25)");
            card.setColor("gray");
            return card;
        }

        // Mock signal generation
        double rand = Math.random();
        if (rand > 0.8) {
            card.setAction("BUY");
            card.setRationale("Indicators Aligned. Bullish volume accumulation.");
            card.setTargetPremium(150.0);
            card.setSlPremium(120.0);
            card.setRecommendedStrike(String.valueOf(Math.round(spotPrice / 100) * 100)); // ATM strike
            card.setPriority("HIGH");
            card.setColor("green");
            card.setAlertTriggered(true);
        } else if (rand < 0.2) {
            card.setAction("SELL");
            card.setRationale("Indicators Aligned. Bearish volume accumulation.");
            card.setTargetPremium(150.0);
            card.setSlPremium(120.0);
            card.setRecommendedStrike(String.valueOf(Math.round(spotPrice / 100) * 100)); // ATM strike
            card.setPriority("HIGH");
            card.setColor("red");
            card.setAlertTriggered(true);
        } else {
            card.setAction("WAITING");
            card.setRationale("Indicators are not aligned yet. Awaiting confluence.");
            card.setColor("gray");
            card.setAlertTriggered(false);
        }
        
        return card;
    }
}
