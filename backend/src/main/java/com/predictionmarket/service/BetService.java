package com.predictionmarket.service;

import com.predictionmarket.model.Bet;
import com.predictionmarket.model.Market;
import com.predictionmarket.model.User;
import com.predictionmarket.repository.BetRepository;
import com.predictionmarket.repository.MarketRepository;
import com.predictionmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BetService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketRepository marketRepository;

    public Bet createBet(Long userId, Long marketId, Bet bet) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found"));

        bet.setUser(user);
        bet.setMarket(market);
        bet.setPlacedAt(LocalDateTime.now());

        return betRepository.save(bet);
    }

    public List<Bet> getAllBets() {
        return betRepository.findAll();
    }

    public List<Bet> getBetsByUser(Long userId) {
        return betRepository.findByUserId(userId);
    }

    public List<Bet> getBetsByMarket(Long marketId) {
        return betRepository.findByMarketId(marketId);
    }
}