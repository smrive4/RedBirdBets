package com.predictionmarket.controller;

import java.util.List;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.predictionmarket.model.Market;
import com.predictionmarket.service.MarketService;

/**
 * Controller for handling market-related requests.
 */
@RestController
@RequestMapping("/api/markets")
public class MarketController {

    @Autowired
    private MarketService marketService;

    // Helper method to check if the current user has admin privileges
    private void requireAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority: authorities){
            if(authority.getAuthority().equals("ADMIN")){
                return;
            } 
        }

        throw new IllegalStateException("Admin access required");
    }

    // Create a new market (admin only)
    @PostMapping
    public Market create(@RequestBody Market market) {
        requireAdmin();
        return marketService.createMarket(market);
    }

    // Get all markets
    @GetMapping
    public List<Market> all() {
        return marketService.getAllMarkets();
    }

    // Get a specific market by ID
    @GetMapping("/{id}")
    public Market one(@PathVariable Long id) {
        return marketService.getMarketById(id);
    }

    // Close a market (admin only)
    @PostMapping("/{id}/close")
    public Market close(@PathVariable Long id) {
        requireAdmin();
        return marketService.closeMarket(id);
    }

    // Resolve a market by specifying the winning option (admin only)
    @PostMapping("/{id}/resolve")
    public void resolve(@PathVariable Long id,
                        @RequestParam Long winningOptionId) {
        requireAdmin();
        marketService.resolveMarket(id, winningOptionId);
    }
}