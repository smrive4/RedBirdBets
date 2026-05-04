package com.predictionmarket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/* Model class representing a bet placed by a user */
@Entity
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "market_id", nullable = false)
    @JsonIgnoreProperties({"options"})
    private Market market;

    // The specific option chosen for this bet
    @ManyToOne
    @JoinColumn(name = "selected_option_id", nullable = false)
    @JsonIgnoreProperties({"market"})
    private MarketOption selectedOption;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime placedAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean won = false;

    @Column(nullable = false, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal payout = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public MarketOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(MarketOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public boolean isWon() {
        return this.won;
    }

    public void setPayout(BigDecimal payout) {
        this.payout = payout;
    }

    public BigDecimal getPayout() {
        return this.payout;
    }
}