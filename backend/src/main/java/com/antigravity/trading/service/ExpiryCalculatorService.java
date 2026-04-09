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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        DayOfWeek targetDay = symbol.equals("NIFTY") ? DayOfWeek.THURSDAY : DayOfWeek.WEDNESDAY;
        LocalDate seedDate = today.with(TemporalAdjusters.nextOrSame(targetDay));

        // Check if today is the target day but past 3:30 PM, then move to next week
        if (today.getDayOfWeek() == targetDay && LocalDateTime.now().getHour() >= 16) {
             seedDate = today.plusWeeks(1).with(TemporalAdjusters.nextOrSame(targetDay));
        }

        for (int i = 0; i < 4; i++) {
            expiries.add(seedDate.plusWeeks(i).format(formatter).toUpperCase());
        }

        String finalExpiry = expiries.get(0);
        LocalDate finalExpiryDate = seedDate;

        if (selectedExpiry != null && !selectedExpiry.isEmpty() && expiries.contains(selectedExpiry)) {
            finalExpiry = selectedExpiry;
            int chunks = expiries.indexOf(selectedExpiry);
            finalExpiryDate = seedDate.plusWeeks(chunks);
        }

        int dte = (int) ChronoUnit.DAYS.between(today, finalExpiryDate);

        return new ExpiryContext(finalExpiry, Math.max(0, dte), expiries);
    }
}
