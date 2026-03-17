package com.predictionmarket.controller;

import com.predictionmarket.model.Market;
import com.predictionmarket.model.Bet;
import com.predictionmarket.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/markets")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @PostMapping
    public Market create(@RequestBody Market market) {
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
    public Market close(@PathVariable Long id) {
        return marketService.closeMarket(id);
    }

    @PostMapping("/{id}/resolve")
    public void resolve(@PathVariable Long id,
                        @RequestParam Bet.BetSide winningSide) {
        marketService.resolveMarket(id, winningSide);
    }
}