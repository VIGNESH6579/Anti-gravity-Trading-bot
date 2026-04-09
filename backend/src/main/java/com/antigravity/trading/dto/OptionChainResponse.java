package com.antigravity.trading.dto;

import java.util.List;

public class OptionChainResponse {
    private String symbol;
    private double spotPrice;
    private double vix;
    private String vixLevel;           // LOW / MODERATE / HIGH / EXTREME
    private String sessionType;        // TRENDING / SIDEWAYS
    private String expiryDate;
    private int daysToExpiry;
    private List<String> availableExpiries;
    private List<StrikeData> chain;
    private Analytics analytics;
    private List<SignalCard> signals;
    private String lastUpdated;        // IST timestamp string

    // --- Nested Classes ---

    public static class StrikeData {
        private double strikePrice;
        private boolean isAtm;
        private OptionData ce;
        private OptionData pe;

        public double getStrikePrice() { return strikePrice; }
        public void setStrikePrice(double strikePrice) { this.strikePrice = strikePrice; }
        public boolean isAtm() { return isAtm; }
        public void setAtm(boolean atm) { isAtm = atm; }
        public OptionData getCe() { return ce; }
        public void setCe(OptionData ce) { this.ce = ce; }
        public OptionData getPe() { return pe; }
        public void setPe(OptionData pe) { this.pe = pe; }
    }

    public static class OptionData {
        private double ltp;
        private double oi;
        private double oiChange;
        private double iv;
        private double delta;
        private double theta;
        private double gamma;
        private double vega;

        public double getLtp() { return ltp; }
        public void setLtp(double ltp) { this.ltp = ltp; }
        public double getOi() { return oi; }
        public void setOi(double oi) { this.oi = oi; }
        public double getOiChange() { return oiChange; }
        public void setOiChange(double oiChange) { this.oiChange = oiChange; }
        public double getIv() { return iv; }
        public void setIv(double iv) { this.iv = iv; }
        public double getDelta() { return delta; }
        public void setDelta(double delta) { this.delta = delta; }
        public double getTheta() { return theta; }
        public void setTheta(double theta) { this.theta = theta; }
        public double getGamma() { return gamma; }
        public void setGamma(double gamma) { this.gamma = gamma; }
        public double getVega() { return vega; }
        public void setVega(double vega) { this.vega = vega; }
    }

    public static class Analytics {
        private double pcr;
        private double maxPain;
        private int ivRank;
        private double orbHigh;
        private double orbLow;
        private boolean orbActive;
        private String oiShiftDirection;  // UP / DOWN / NEUTRAL
        private double oiShiftStrike;
        private double vwap;

        public double getPcr() { return pcr; }
        public void setPcr(double pcr) { this.pcr = pcr; }
        public double getMaxPain() { return maxPain; }
        public void setMaxPain(double maxPain) { this.maxPain = maxPain; }
        public int getIvRank() { return ivRank; }
        public void setIvRank(int ivRank) { this.ivRank = ivRank; }
        public double getOrbHigh() { return orbHigh; }
        public void setOrbHigh(double orbHigh) { this.orbHigh = orbHigh; }
        public double getOrbLow() { return orbLow; }
        public void setOrbLow(double orbLow) { this.orbLow = orbLow; }
        public boolean isOrbActive() { return orbActive; }
        public void setOrbActive(boolean orbActive) { this.orbActive = orbActive; }
        public String getOiShiftDirection() { return oiShiftDirection; }
        public void setOiShiftDirection(String oiShiftDirection) { this.oiShiftDirection = oiShiftDirection; }
        public double getOiShiftStrike() { return oiShiftStrike; }
        public void setOiShiftStrike(double oiShiftStrike) { this.oiShiftStrike = oiShiftStrike; }
        public double getVwap() { return vwap; }
        public void setVwap(double vwap) { this.vwap = vwap; }
    }

    public static class SignalCard {
        private String strategyName;
        private String action;          // BUY / SELL / NEUTRAL / WAITING / BLOCKED
        private String rationale;
        private Double targetPremium;
        private Double slPremium;
        private String recommendedStrike;
        private String priority;        // HIGH / MEDIUM / LOW
        private boolean alertTriggered;
        private String color;           // green / red / yellow / gray
        private String timestamp;       // trigger time

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getStrategyName() { return strategyName; }
        public void setStrategyName(String strategyName) { this.strategyName = strategyName; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getRationale() { return rationale; }
        public void setRationale(String rationale) { this.rationale = rationale; }
        public Double getTargetPremium() { return targetPremium; }
        public void setTargetPremium(Double targetPremium) { this.targetPremium = targetPremium; }
        public Double getSlPremium() { return slPremium; }
        public void setSlPremium(Double slPremium) { this.slPremium = slPremium; }
        public String getRecommendedStrike() { return recommendedStrike; }
        public void setRecommendedStrike(String recommendedStrike) { this.recommendedStrike = recommendedStrike; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public boolean isAlertTriggered() { return alertTriggered; }
        public void setAlertTriggered(boolean alertTriggered) { this.alertTriggered = alertTriggered; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    // --- Root Getters/Setters ---
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public double getSpotPrice() { return spotPrice; }
    public void setSpotPrice(double spotPrice) { this.spotPrice = spotPrice; }
    public double getVix() { return vix; }
    public void setVix(double vix) { this.vix = vix; }
    public String getVixLevel() { return vixLevel; }
    public void setVixLevel(String vixLevel) { this.vixLevel = vixLevel; }
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public int getDaysToExpiry() { return daysToExpiry; }
    public void setDaysToExpiry(int daysToExpiry) { this.daysToExpiry = daysToExpiry; }
    public List<String> getAvailableExpiries() { return availableExpiries; }
    public void setAvailableExpiries(List<String> availableExpiries) { this.availableExpiries = availableExpiries; }
    public List<StrikeData> getChain() { return chain; }
    public void setChain(List<StrikeData> chain) { this.chain = chain; }
    public Analytics getAnalytics() { return analytics; }
    public void setAnalytics(Analytics analytics) { this.analytics = analytics; }
    public List<SignalCard> getSignals() { return signals; }
    public void setSignals(List<SignalCard> signals) { this.signals = signals; }
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
