package com.antigravity.trading.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpiryCalculatorService {

    public static class ExpiryContext {
        public String currentExpiryStr;
        public int daysToExpiry;
        public List<String> availableExpiries;

        public ExpiryContext(String currentExpiryStr, int daysToExpiry, List<String> availableExpiries) {
            this.currentExpiryStr = currentExpiryStr;
            this.daysToExpiry = daysToExpiry;
            this.availableExpiries = availableExpiries;
        }
    }

    public ExpiryContext calculateExpiries(String symbol, String selectedExpiry) {
        LocalDate today = LocalDate.now();
        List<String> expiries = new ArrayList<>();
        List<LocalDate> expiryDates = new ArrayList<>();
        DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        boolean apiSuccess = false;
        try {
            String growwSymbol = symbol.equalsIgnoreCase("BANKNIFTY") ? "banknifty" : "nifty";
            String url = "https://groww.in/v1/api/option_chain_service/v1/option_chain/" + growwSymbol;
            
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>("parameters", headers);
            
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, java.util.Map.class);
            java.util.Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("optionChain")) {
                java.util.Map<String, Object> optionChain = (java.util.Map<String, Object>) body.get("optionChain");
                if (optionChain.containsKey("expiryDetailsDto")) {
                    java.util.Map<String, Object> expiryDetailsDto = (java.util.Map<String, Object>) optionChain.get("expiryDetailsDto");
                    List<String> growwDates = (List<String>) expiryDetailsDto.get("expiryDates");
                    
                    if (growwDates != null && !growwDates.isEmpty()) {
                        for (int i = 0; i < Math.min(6, growwDates.size()); i++) {
                            LocalDate d = LocalDate.parse(growwDates.get(i), inFormatter);
                            if (!d.isBefore(today)) { // Skip past dates if cached
                                expiryDates.add(d);
                                expiries.add(d.format(outFormatter).toUpperCase());
                            }
                        }
                        if (!expiries.isEmpty()) {
                            apiSuccess = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Fallback to math calculateExpiries. Groww API Failed: " + e.getMessage());
        }

        if (!apiSuccess) {
            // Fallback: Mathematical logic
            if (symbol.equals("NIFTY")) {
                LocalDate seedDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
                if (today.getDayOfWeek() == DayOfWeek.THURSDAY && LocalDateTime.now().getHour() >= 16) {
                    seedDate = today.plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
                }
                for (int i = 0; i < 4; i++) {
                    LocalDate d = seedDate.plusWeeks(i);
                    expiryDates.add(d);
                    expiries.add(d.format(outFormatter).toUpperCase());
                }
            } else {
                LocalDate seedDate = today.with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
                if (today.isAfter(seedDate) || (today.isEqual(seedDate) && LocalDateTime.now().getHour() >= 16)) {
                    seedDate = today.plusMonths(1).with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
                }
                for (int i = 0; i < 4; i++) {
                    LocalDate d = seedDate.plusMonths(i).with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
                    expiryDates.add(d);
                    expiries.add(d.format(outFormatter).toUpperCase());
                }
            }
        }

        String finalExpiry = expiries.get(0);
        LocalDate finalExpiryDate = expiryDates.get(0);

        if (selectedExpiry != null && !selectedExpiry.isEmpty() && expiries.contains(selectedExpiry)) {
            int index = expiries.indexOf(selectedExpiry);
            finalExpiry = expiries.get(index);
            finalExpiryDate = expiryDates.get(index);
        }

        int dte = (int) ChronoUnit.DAYS.between(today, finalExpiryDate);

        return new ExpiryContext(finalExpiry, Math.max(0, dte), expiries);
    }
}
