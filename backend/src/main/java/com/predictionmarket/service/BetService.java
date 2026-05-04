package com.predictionmarket.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.predictionmarket.dto.LeaderboardEntryDTO;
import com.predictionmarket.model.Bet;
import com.predictionmarket.model.Market;
import com.predictionmarket.model.MarketOption;
import com.predictionmarket.model.User;
import com.predictionmarket.repository.BetRepository;
import com.predictionmarket.repository.MarketOptionRepository;
import com.predictionmarket.repository.MarketRepository;
import com.predictionmarket.repository.UserRepository;

/* Service class for handling bet-related operations */
@Service
public class BetService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private MarketOptionRepository marketOptionRepository;

    public List<Bet> getAllBets() {
        return betRepository.findAll();
    }

    public List<Bet> getBetsByUser(Long userId) {
        return betRepository.findByUserId(userId);
    }

    public List<Bet> getBetsByMarket(Long marketId) {
        return betRepository.findByMarketId(marketId);
    }

    @Transactional
    public Bet createBet(Long userId, Long marketId, Bet bet) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found"));

        // Ensure market is open
        if (market.getStatus() != Market.MarketStatus.OPEN) {
            throw new IllegalStateException("Market is closed");
        }

        // Validate bet amount
        if (bet.getAmount() == null || bet.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be greater than 0");
        }

        // Ensure selected option exists in request
        if (bet.getSelectedOption() == null || bet.getSelectedOption().getId() == null) {
            throw new IllegalArgumentException("Selected option is required");
        }

        MarketOption selectedOption = marketOptionRepository.findById(bet.getSelectedOption().getId())
                .orElseThrow(() -> new IllegalArgumentException("Option not found"));

        // Ensure selected option belongs to this market
        if (selectedOption.getMarket() == null || !selectedOption.getMarket().getId().equals(marketId)) {
            throw new IllegalArgumentException("Option does not belong to this market");
        }

        // Ensure balance exists
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.ZERO);
        }

        // Check balance
        if (user.getBalance().compareTo(bet.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Deduct balance
        user.setBalance(user.getBalance().subtract(bet.getAmount()));

        // Add amount to selected option pool
        selectedOption.addToTotalAmount(bet.getAmount());

        // Set bet info
        bet.setUser(user);
        bet.setMarket(market);
        bet.setSelectedOption(selectedOption);
        bet.setPlacedAt(LocalDateTime.now());
        bet.setWon(false);
        bet.setPayout(BigDecimal.ZERO);

        // Save updates
        marketOptionRepository.save(selectedOption);
        userRepository.save(user);
        return betRepository.save(bet);
    }

    public List<LeaderboardEntryDTO> getLeaderboardByWinningsDesc() {
        return betRepository.getWinningsByDesc();
    }

    public List<LeaderboardEntryDTO> getLeaderboardByLossesDesc() {
        return betRepository.getLossesByDesc();
    }

    public List<LeaderboardEntryDTO> getLeaderboardByMonthlyLossesDesc() {
        LocalDateTime dt = LocalDateTime.now();
        return betRepository.getLossesByMonthlyDesc(
            dt.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS)
        );
    }
}