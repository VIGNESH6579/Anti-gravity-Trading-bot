package com.antigravity.trading.service;

import com.antigravity.trading.dto.OptionChainResponse.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SignalEngineService {

    @Autowired
    private SuperTrendVAPIService superTrendVAPIService;
    @Autowired
    private GammaScalpService gammaScalpService;
    @Autowired
    private ORBService orbService;
    @Autowired
    private MaxPainGravityService maxPainGravityService;
    @Autowired
    private OIShiftDetectorService oiShiftDetectorService;
    @Autowired
    private VwapMeanReversionService vwapMeanReversionService;
    @Autowired
    private AlertDispatcherService alertDispatcherService;

    public List<SignalCard> generateAllSignals(String symbol, double spotPrice, double vix, String sessionType, int dte, Analytics analytics) {
        List<SignalCard> signals = new ArrayList<>();

        // Run all strategies
        SignalCard stCard = superTrendVAPIService.evaluate(spotPrice, vix);
        signals.add(stCard);

        SignalCard gammaCard = gammaScalpService.evaluate(spotPrice, dte);
        signals.add(gammaCard);

        SignalCard orbCard = orbService.evaluate(spotPrice, sessionType, vix);
        signals.add(orbCard);

        SignalCard mpGravityCard = maxPainGravityService.evaluate(spotPrice, analytics.getMaxPain(), dte);
        signals.add(mpGravityCard);

        // For OI shift, we approximate highest OI strike finding
        double maxOiStrike = analytics.getOiShiftStrike() > 0 ? analytics.getOiShiftStrike() : Math.round(spotPrice / 100) * 100;
        SignalCard oiShiftCard = oiShiftDetectorService.evaluate(maxOiStrike);
        signals.add(oiShiftCard);

        SignalCard vwapCard = vwapMeanReversionService.evaluate(spotPrice, analytics.getVwap(), sessionType);
        signals.add(vwapCard);

        // Dispatch alerts for any triggered signals
        for (SignalCard card : signals) {
            if (card.isAlertTriggered()) {
                alertDispatcherService.dispatch(card, symbol);
                // Prevent duplicate alerts immediately (basic throttle)
                card.setAlertTriggered(false); 
            }
        }

        return signals;
    }
}
