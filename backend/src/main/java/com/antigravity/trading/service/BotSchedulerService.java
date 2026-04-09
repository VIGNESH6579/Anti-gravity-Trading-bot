package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BotSchedulerService {

    @Autowired
    private AngelOptionChainService chainService;
    @Autowired
    private ExpiryCalculatorService expiryService;
    @Autowired
    private VixService vixService;
    @Autowired
    private SessionClassifierService sessionService;
    @Autowired
    private SignalEngineService signalEngine;

    // Run every 30 seconds to evaluate strategies headlessly
    @Scheduled(fixedRate = 30000)
    public void evaluateMarketsHeadless() {
        // Skip scheduling logic if it's weekend, etc., in production.
        processSymbol("NIFTY");
        processSymbol("BANKNIFTY");
    }

    private void processSymbol(String symbol) {
        try {
            double spotPrice = chainService.fetchSpotPrice(symbol);
            double vix = vixService.getCurrentVix();
            String sessionType = sessionService.classifySession();
            
            ExpiryCalculatorService.ExpiryContext ctx = expiryService.calculateExpiries(symbol, null);
            
            // Reusing the same analytics mockup for headless evaluation
            OptionChainResponse.Analytics analytics = new OptionChainResponse.Analytics();
            analytics.setPcr(1.34);
            analytics.setMaxPain(Math.round(spotPrice / 100) * 100);
            analytics.setIvRank(42);
            analytics.setVwap(spotPrice - 10);
            
            // GenerateallSignals automatically invokes the AlertDispatcherService
            // if any strategy conditions line up, which pushes the notification to ntfy.sh
            signalEngine.generateAllSignals(
                symbol, spotPrice, vix, sessionType, ctx.daysToExpiry, analytics);
                
        } catch (Exception e) {
            System.err.println("Error evaluating headless signals for " + symbol + ": " + e.getMessage());
        }
    }
}
