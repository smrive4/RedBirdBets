package com.predictionmarket.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
/* Model class representing an option within a market, such as "YES", "NO", "US", "Germany", etc. */
@Entity
@Table(name = "market_options")
public class MarketOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: "YES", "NO", "US", "Germany", etc.
    @Column(nullable = false)
    private String optionName;

    // Total money placed on this option
    @Column(nullable = false, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Link back to market
    @ManyToOne
    @JoinColumn(name = "market_id", nullable = false)
    @JsonIgnoreProperties({"options"})
    private Market market;

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Add money to this option (used when placing bets)
    public void addToTotalAmount(BigDecimal amount) {
        if (amount != null) {
            this.totalAmount = this.totalAmount.add(amount);
        }
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }
}