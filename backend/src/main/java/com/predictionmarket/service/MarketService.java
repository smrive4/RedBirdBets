package com.predictionmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.predictionmarket.model.Bet;
import com.predictionmarket.model.Market;
import com.predictionmarket.model.User;
import com.predictionmarket.repository.BetRepository;
import com.predictionmarket.repository.MarketRepository;
import com.predictionmarket.repository.UserRepository;

@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    // Create market
    public Market createMarket(Market market) {
        return marketRepository.save(market);
    }

    // Get all markets
    public List<Market> getAllMarkets() {
        return marketRepository.findAll();
    }

    // Get market by ID
    public Market getMarketById(Long id) {
        return marketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Market not found"));
    }

    // Close market without payout
    public Market closeMarket(Long id) {
        Market market = getMarketById(id);
        market.setStatus(Market.MarketStatus.CLOSED);
        return marketRepository.save(market);
    }

    // Resolve market + dynamic payout
    @Transactional
    public void resolveMarket(Long marketId, Bet.BetSide winningSide) {

        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found"));

        // Ensure market is open
        if (market.getStatus() != Market.MarketStatus.OPEN) {
            throw new IllegalStateException("Market already closed");
        }

        // Set result
        market.setWinningSide(winningSide);
        market.setStatus(Market.MarketStatus.CLOSED);

        List<Bet> bets = betRepository.findByMarketId(marketId);

        // Use stored totals instead of recalculating
        BigDecimal totalPool = market.getTotalYesAmt().add(market.getTotalNoAmt());

        BigDecimal winningPool = (winningSide == Bet.BetSide.YES)
                ? market.getTotalYesAmt()
                : market.getTotalNoAmt();

        // Reset all bets
        for (Bet bet : bets) {
            bet.setWon(false);
            bet.setPayout(BigDecimal.ZERO);
        }

        // Pay winners proportionally
        if (winningPool.compareTo(BigDecimal.ZERO) > 0) {

            for (Bet bet : bets) {
                if (bet.getSide() == winningSide) {

                    BigDecimal payout = bet.getAmount()
                            .multiply(totalPool)
                            .divide(winningPool, 2, RoundingMode.HALF_UP);

                    bet.setPayout(payout);
                    bet.setWon(true);

                    User user = bet.getUser();

                    if (user.getBalance() == null) {
                        user.setBalance(BigDecimal.ZERO);
                    }

                    user.setBalance(user.getBalance().add(payout));
                    userRepository.save(user);
                }
            }
        }

        // Save updates
        marketRepository.save(market);
        betRepository.saveAll(bets);
    }

    // Optional: get live odds
    public BigDecimal getYesMultiplier(Long marketId) {
        Market market = getMarketById(marketId);
        BigDecimal total = market.getTotalYesAmt().add(market.getTotalNoAmt());

        if (market.getTotalYesAmt().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return total.divide(market.getTotalYesAmt(), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getNoMultiplier(Long marketId) {
        Market market = getMarketById(marketId);
        BigDecimal total = market.getTotalYesAmt().add(market.getTotalNoAmt());

        if (market.getTotalNoAmt().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return total.divide(market.getTotalNoAmt(), 2, RoundingMode.HALF_UP);
    }
}