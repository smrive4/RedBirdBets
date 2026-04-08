package com.predictionmarket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetSide side;

    @Column(nullable = false)
    private LocalDateTime placedAt;

    @Column(nullable = false, columnDefinition= "boolean default false")
    private boolean won = false;

    @Column(nullable = false, columnDefinition= "decimal(10,2) default 0.00")
    private BigDecimal payout = BigDecimal.ZERO;

    public enum BetSide {
        YES, NO
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BetSide getSide() {
        return side;
    }

    public void setSide(BetSide side) {
        this.side = side;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public void setWon(boolean won){
        this.won = won;
    }

    public boolean isWon(){
        return this.won;
    }

    public void setPayout(BigDecimal payout){
        this.payout = payout;
    }

    public BigDecimal getPayout(){
        return this.payout;
    }

}