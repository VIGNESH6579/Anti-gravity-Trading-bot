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

        if (symbol.equals("NIFTY")) {
            // NIFTY: Weekly expiries on Thursday
            LocalDate seedDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
            if (today.getDayOfWeek() == DayOfWeek.THURSDAY && LocalDateTime.now().getHour() >= 16) {
                seedDate = today.plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
            }
            for (int i = 0; i < 4; i++) {
                expiries.add(seedDate.plusWeeks(i).format(formatter).toUpperCase());
            }
        } else {
            // BANKNIFTY: SEBI 2024 Rules - Only Monthly Expiries (Last Wednesday of the month)
            LocalDate seedDate = today.with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
            
            // If the last Wednesday of this month has already passed, start with next month
            if (today.isAfter(seedDate) || (today.isEqual(seedDate) && LocalDateTime.now().getHour() >= 16)) {
                seedDate = today.plusMonths(1).with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
            }
            
            for (int i = 0; i < 4; i++) {
                expiries.add(seedDate.plusMonths(i).with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY)).format(formatter).toUpperCase());
            }
        }

        String finalExpiry = expiries.get(0);
        LocalDate finalExpiryDate = LocalDate.parse(finalExpiry, formatter);

        if (selectedExpiry != null && !selectedExpiry.isEmpty() && expiries.contains(selectedExpiry)) {
            finalExpiry = selectedExpiry;
            finalExpiryDate = LocalDate.parse(finalExpiry, formatter);
        }

        int dte = (int) ChronoUnit.DAYS.between(today, finalExpiryDate);

        return new ExpiryContext(finalExpiry, Math.max(0, dte), expiries);
    }
}
