package com.predictionmarket.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.predictionmarket.dto.LeaderboardEntryDTO;
import com.predictionmarket.model.Bet;
import com.predictionmarket.model.Market;
import com.predictionmarket.model.User;
import com.predictionmarket.repository.BetRepository;
import com.predictionmarket.repository.MarketRepository;
import com.predictionmarket.repository.UserRepository;

@Service
public class BetService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketRepository marketRepository;

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
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Market market = marketRepository.findById(marketId).orElseThrow(() -> new IllegalArgumentException("Market not found"));

        // Validate market is OPEN
        if (market.getStatus() != Market.MarketStatus.OPEN) {
            throw new IllegalStateException("Market is closed");
        }

        // Validate bet amount
        if (bet.getAmount() == null || bet.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be greater than 0");
        }

        // Validate bet side
        if (bet.getSide() == null) {
            throw new IllegalArgumentException("Bet side must be YES or NO");
        }

        // Ensure balance is not null
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.ZERO);
        }

        // Validate balance
        if (user.getBalance().compareTo(bet.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Deduct balance
        user.setBalance(user.getBalance().subtract(bet.getAmount()));

        // Set relationships + timestamp
        bet.setUser(user);
        bet.setMarket(market);
        bet.setPlacedAt(LocalDateTime.now());

        // Save
        userRepository.save(user);
        return betRepository.save(bet);
    }

    public List<LeaderboardEntryDTO> getLeaderboardByWinningsDesc(){
        return betRepository.getWinningsByDesc();
    }

    public List<LeaderboardEntryDTO> getLeaderboardByLossesDesc(){
        return betRepository.getLossesByDesc();
    }
}
