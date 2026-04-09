package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.SignalCard;
import org.springframework.stereotype.Service;

@Service
public class OIShiftDetectorService {

    private double lastHighestOiStrike = 0;

    public SignalCard evaluate(double currentHighestOiStrike) {
        SignalCard card = new SignalCard();
        card.setStrategyName("OI Shift Detection");
        
        if (lastHighestOiStrike == 0) {
            lastHighestOiStrike = currentHighestOiStrike;
            card.setAction("WAITING");
            card.setRationale("Initializing OI baseline.");
            card.setColor("gray");
            return card;
        }
        
        if (currentHighestOiStrike > lastHighestOiStrike) {
            card.setAction("BUY");
            card.setRationale("Highest OI strike shifted UP. Bullish adjustment by writers.");
            card.setColor("green");
            card.setAlertTriggered(true);
        } else if (currentHighestOiStrike < lastHighestOiStrike) {
            card.setAction("SELL");
            card.setRationale("Highest OI strike shifted DOWN. Bearish adjustment.");
            card.setColor("red");
            card.setAlertTriggered(true);
        } else {
            card.setAction("NEUTRAL");
            card.setRationale("No major OI shift detected.");
            card.setColor("gray");
        }
        
        lastHighestOiStrike = currentHighestOiStrike;
        return card;
    }
}
