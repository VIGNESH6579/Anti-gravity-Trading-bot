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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        if (symbol.equals("NIFTY")) {
            LocalDate seedDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
            if (today.getDayOfWeek() == DayOfWeek.THURSDAY && LocalDateTime.now().getHour() >= 16) {
                seedDate = today.plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
            }
            for (int i = 0; i < 4; i++) {
                LocalDate d = seedDate.plusWeeks(i);
                expiryDates.add(d);
                expiries.add(d.format(formatter).toUpperCase());
            }
        } else {
            LocalDate seedDate = today.with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
            if (today.isAfter(seedDate) || (today.isEqual(seedDate) && LocalDateTime.now().getHour() >= 16)) {
                seedDate = today.plusMonths(1).with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
            }
            for (int i = 0; i < 4; i++) {
                LocalDate d = seedDate.plusMonths(i).with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
                expiryDates.add(d);
                expiries.add(d.format(formatter).toUpperCase());
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
