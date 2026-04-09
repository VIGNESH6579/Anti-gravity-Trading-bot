package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.OptionData;
import com.antigravity.trading.dto.OptionChainResponse.StrikeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AngelOptionChainService {

    @Autowired
    private AngelAuthService authService;

    // Simulation logic to build the Option Chain if API is rate-limited or not available yet
    public List<StrikeData> fetchSimulatedChain(String symbol, double spotPrice) {
        List<StrikeData> chain = new ArrayList<>();
        int interval = symbol.equals("NIFTY") ? 50 : 100;
        int atmStrike = (int) (Math.round(spotPrice / interval) * interval);
        
        int strikesToGenerate = 10;
        int startStrike = atmStrike - (strikesToGenerate * interval);
        int endStrike = atmStrike + (strikesToGenerate * interval);

        for (int strike = startStrike; strike <= endStrike; strike += interval) {
            StrikeData row = new StrikeData();
            row.setStrikePrice(strike);
            row.setAtm(strike == atmStrike);

            OptionData ce = generateMockOption(true, spotPrice, strike);
            OptionData pe = generateMockOption(false, spotPrice, strike);
            
            row.setCe(ce);
            row.setPe(pe);
            
            chain.add(row);
        }

        return chain;
    }

    private OptionData generateMockOption(boolean isCall, double spot, double strike) {
        OptionData data = new OptionData();
        double intrinsic = isCall ? Math.max(0, spot - strike) : Math.max(0, strike - spot);
        double timeValue = Math.random() * 50 + 20; // random time value
        
        data.setLtp(Math.round((intrinsic + timeValue) * 100.0) / 100.0);
        data.setOi(Math.round(Math.random() * 500000 + 10000));
        data.setOiChange(Math.round((Math.random() - 0.5) * 50000));
        data.setIv(Math.round((Math.random() * 15 + 10) * 100.0) / 100.0);
        
        return data;
    }

    public double fetchSpotPrice(String symbol) {
        try {
            String ticker = symbol.equals("NIFTY") ? "^NSEI" : "^NSEBANK";
            String url = "https://query2.finance.yahoo.com/v8/finance/chart/" + ticker;
            
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            headers.set("Accept", "application/json");
            
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>("parameters", headers);
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.GET, 
                entity, 
                java.util.Map.class
            );
            
            java.util.Map<String, Object> chart = (java.util.Map<String, Object>) response.getBody().get("chart");
            java.util.List<java.util.Map<String, Object>> result = (java.util.List<java.util.Map<String, Object>>) chart.get("result");
            java.util.Map<String, Object> meta = (java.util.Map<String, Object>) result.get(0).get("meta");
            Number priceObj = (Number) meta.get("regularMarketPrice");
            
            return Math.round(priceObj.doubleValue() * 100.0) / 100.0;
            
        } catch (Exception e) {
            System.err.println("Failed to fetch real spot price for " + symbol + ": " + e.getMessage());
            // Fallback
            double base = symbol.equals("NIFTY") ? 24000.0 : 51000.0;
            return base + (Math.random() - 0.5) * 200;
        }
    }
}
