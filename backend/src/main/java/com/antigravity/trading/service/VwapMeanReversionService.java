package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.SignalCard;
import org.springframework.stereotype.Service;

@Service
public class VwapMeanReversionService {

    public SignalCard evaluate(double spotPrice, double vwap, String sessionType) {
        SignalCard card = new SignalCard();
        card.setStrategyName("VWAP Mean Reversion");
        
        if (!"SIDEWAYS".equals(sessionType)) {
            card.setAction("WAITING");
            card.setRationale("Active only on SIDEWAYS days. Current session is TRENDING.");
            card.setColor("gray");
            return card;
        }

        double diff = spotPrice - vwap;
        double threshold = 75; // Approx 1 ATR mock value

        if (diff > threshold) {
            card.setAction("SELL");
            card.setRationale("Price stretched 1+ ATR above VWAP. Fading downward.");
            card.setColor("red");
            card.setAlertTriggered(true);
            card.setTargetPremium(vwap);
        } else if (diff < -threshold) {
            card.setAction("BUY");
            card.setRationale("Price stretched 1+ ATR below VWAP. Fading upward.");
            card.setColor("green");
            card.setAlertTriggered(true);
            card.setTargetPremium(vwap);
        } else {
            card.setAction("WAITING");
            card.setRationale("Price interacting near VWAP. No stretch detected.");
            card.setColor("gray");
        }

        return card;
    }
}
