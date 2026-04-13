package com.predictionmarket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.predictionmarket.model.Bet;
import com.predictionmarket.model.Market;
import com.predictionmarket.model.User;
import com.predictionmarket.service.MarketService;
import com.predictionmarket.service.UserService;

@RestController
@RequestMapping("/api/markets")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @Autowired
    private UserService userService;

    private void requireAdmin(Long requesterId) {
        User requester = userService.getUserById(requesterId);
        if (!requester.getRole().equals("ADMIN")) {
            throw new IllegalStateException("Admin access required");
        }
    }

    @PostMapping
    public Market create(@RequestBody Market market, @RequestParam Long requesterId) {
        requireAdmin(requesterId);
        return marketService.createMarket(market);
    }

    @GetMapping
    public List<Market> all() {
        return marketService.getAllMarkets();
    }

    @GetMapping("/{id}")
    public Market one(@PathVariable Long id) {
        return marketService.getMarketById(id);
    }

    @PostMapping("/{id}/close")
    public Market close(@PathVariable Long id, @RequestParam Long requesterId) {
        requireAdmin(requesterId);
        return marketService.closeMarket(id);
    }

    @PostMapping("/{id}/resolve")
    public void resolve(@PathVariable Long id,
                        @RequestParam Bet.BetSide winningSide,
                        @RequestParam Long requesterId) {
        requireAdmin(requesterId);
        marketService.resolveMarket(id, winningSide);
    }
}