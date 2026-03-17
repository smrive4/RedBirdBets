package com.predictionmarket.service;

import com.predictionmarket.model.Bet;
import com.predictionmarket.model.Market;
import com.predictionmarket.model.User;
import com.predictionmarket.repository.BetRepository;
import com.predictionmarket.repository.MarketRepository;
import com.predictionmarket.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
        return marketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Market not found"));
    }

    // Close market manually (optional utility)
    public Market closeMarket(Long id) {
        Market market = getMarketById(id);
        market.setStatus(Market.MarketStatus.CLOSED);
        return marketRepository.save(market);
    }

    // Resolve market + payout
    @Transactional
    public void resolveMarket(Long marketId, Bet.BetSide winningSide) {

        Market market = marketRepository.findById(marketId).orElseThrow(() -> new IllegalArgumentException("Market not found"));

        if (market.getStatus() != Market.MarketStatus.OPEN) {
            throw new IllegalStateException("Market already closed");
        }

        // set result
        market.setWinningSide(winningSide);
        market.setStatus(Market.MarketStatus.CLOSED);

        // get all bets
        List<Bet> bets = betRepository.findByMarketId(marketId);

        BigDecimal totalPool = BigDecimal.ZERO;
        BigDecimal winningPool = BigDecimal.ZERO;

        // calculate pools
        for (Bet bet : bets) {
            totalPool = totalPool.add(bet.getAmount());

            if (bet.getSide() == winningSide) {
                winningPool = winningPool.add(bet.getAmount());
            }
        }

        // payout if winners exist
        if (winningPool.compareTo(BigDecimal.ZERO) > 0) {

            for (Bet bet : bets) {
                if (bet.getSide() == winningSide) {

                    User user = bet.getUser();

                    BigDecimal payout = bet.getAmount().divide(winningPool, 8, RoundingMode.HALF_UP).multiply(totalPool);

                    user.setBalance(user.getBalance().add(payout));
                    userRepository.save(user);
                }
            }
        }

        // save market state
        marketRepository.save(market);
    }
}