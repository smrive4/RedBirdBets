package com.predictionmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.predictionmarket.model.Bet;
import com.predictionmarket.model.Market;
import com.predictionmarket.model.MarketOption;
import com.predictionmarket.model.User;
import com.predictionmarket.repository.BetRepository;
import com.predictionmarket.repository.MarketOptionRepository;
import com.predictionmarket.repository.MarketRepository;
import com.predictionmarket.repository.UserRepository;

/* Service class for handling market-related operations */
@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketOptionRepository marketOptionRepository;

    // Create market
    public Market createMarket(Market market) {
        if (market.getOptions() == null || market.getOptions().size() < 2) {
            throw new IllegalArgumentException("A market must have at least 2 options");
        }

        for (MarketOption option : market.getOptions()) {
            option.setMarket(market);

            if (option.getTotalAmount() == null) {
                option.setTotalAmount(BigDecimal.ZERO);
            }
        }

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
    public void resolveMarket(Long marketId, Long winningOptionId) {

        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found"));

        // Ensure market is open
        if (market.getStatus() != Market.MarketStatus.OPEN) {
            throw new IllegalStateException("Market already closed");
        }

        MarketOption winningOption = marketOptionRepository.findById(winningOptionId)
                .orElseThrow(() -> new IllegalArgumentException("Winning option not found"));

        // Ensure winning option belongs to this market
        if (winningOption.getMarket() == null || !winningOption.getMarket().getId().equals(marketId)) {
            throw new IllegalArgumentException("Winning option does not belong to this market");
        }

        market.setWinningOptionId(winningOptionId);
        market.setStatus(Market.MarketStatus.CLOSED);

        List<Bet> bets = betRepository.findByMarketId(marketId);
        List<MarketOption> options = marketOptionRepository.findByMarketId(marketId);

        BigDecimal totalPool = BigDecimal.ZERO;
        for (MarketOption option : options) {
            totalPool = totalPool.add(option.getTotalAmount());
        }

        BigDecimal winningPool = winningOption.getTotalAmount();

        // Reset all bets
        for (Bet bet : bets) {
            bet.setWon(false);
            bet.setPayout(BigDecimal.ZERO);
        }

        // Pay winners proportionally
        if (winningPool.compareTo(BigDecimal.ZERO) > 0) {
            for (Bet bet : bets) {
                if (bet.getSelectedOption() != null
                        && bet.getSelectedOption().getId().equals(winningOptionId)) {

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

    // Optional: get live odds for one option
    public BigDecimal getOptionMultiplier(Long marketId, Long optionId) {
        MarketOption option = marketOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("Option not found"));

        if (option.getMarket() == null || !option.getMarket().getId().equals(marketId)) {
            throw new IllegalArgumentException("Option does not belong to this market");
        }

        List<MarketOption> options = marketOptionRepository.findByMarketId(marketId);

        BigDecimal totalPool = BigDecimal.ZERO;
        for (MarketOption currentOption : options) {
            totalPool = totalPool.add(currentOption.getTotalAmount());
        }

        if (option.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalPool.divide(option.getTotalAmount(), 2, RoundingMode.HALF_UP);
    }
}