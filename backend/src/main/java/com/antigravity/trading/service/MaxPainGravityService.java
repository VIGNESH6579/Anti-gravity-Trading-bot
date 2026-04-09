package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.SignalCard;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class MaxPainGravityService {

    public SignalCard evaluate(double spotPrice, double maxPain, int dte) {
        SignalCard card = new SignalCard();
        card.setStrategyName("Max Pain Gravity Reversal");
        
        if (dte != 0) {
            card.setAction("WAITING");
            card.setRationale("Only active on expiry days (0 DTE).");
            card.setColor("gray");
            return card;
        }
        
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(14, 30))) {
            card.setAction("WAITING");
            card.setRationale("Active only after 2:30 PM.");
            card.setColor("gray");
            return card;
        }
        
        double difference = spotPrice - maxPain;
        
        if (difference > 150) {
            card.setAction("SELL");
            card.setRationale("Spot is 150+ pts above Max Pain. Mean reverting downward expected.");
            card.setColor("red");
            card.setAlertTriggered(true);
        } else if (difference < -150) {
            card.setAction("BUY");
            card.setRationale("Spot is 150+ pts below Max Pain. Mean reverting upward expected.");
            card.setColor("green");
            card.setAlertTriggered(true);
        } else {
            card.setAction("NEUTRAL");
            card.setRationale("Spot near Max Pain horizon.");
            card.setColor("gray");
        }
        
        return card;
    }
}
