package com.antigravity.trading.controller;

import com.antigravity.trading.dto.OptionChainResponse;
import com.antigravity.trading.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/option-chain")
public class OptionChainController {

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

    @GetMapping
    public ResponseEntity<OptionChainResponse> getChain(
            @RequestParam(defaultValue = "NIFTY") String symbol,
            @RequestParam(required = false) String expiry) {
        
        OptionChainResponse response = new OptionChainResponse();
        
        // 1. Basic Info
        double spotPrice = chainService.fetchSpotPrice(symbol);
        response.setSymbol(symbol);
        response.setSpotPrice(spotPrice);
        
        double vix = vixService.getCurrentVix();
        response.setVix(vix);
        response.setVixLevel(vixService.getVixLevel(vix));
        
        String sessionType = sessionService.classifySession();
        response.setSessionType(sessionType);
        
        // 2. Expiry Info
        ExpiryCalculatorService.ExpiryContext ctx = expiryService.calculateExpiries(symbol, expiry);
        response.setExpiryDate(ctx.currentExpiryStr);
        response.setDaysToExpiry(ctx.daysToExpiry);
        response.setAvailableExpiries(ctx.availableExpiries);
        
        // 3. Option Chain Data
        List<OptionChainResponse.StrikeData> chainData = chainService.fetchSimulatedChain(symbol, spotPrice);
        response.setChain(chainData);
        
        // 4. Analytics
        OptionChainResponse.Analytics analytics = new OptionChainResponse.Analytics();
        analytics.setPcr(1.34);
        analytics.setMaxPain(Math.round(spotPrice / 100) * 100);
        analytics.setIvRank(42);
        analytics.setVwap(spotPrice - 10);
        response.setAnalytics(analytics);
        
        // 5. Signals
        List<OptionChainResponse.SignalCard> signals = signalEngine.generateAllSignals(
                symbol, spotPrice, vix, sessionType, ctx.daysToExpiry, analytics);
        response.setSignals(signals);

        // 6. IST Timestamp
        response.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a 'IST'")));

        return ResponseEntity.ok(response);
    }
}
