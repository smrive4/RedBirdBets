package com.predictionmarket.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "markets")
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(updatable = false)
    private Instant createdAt;

    @Column(nullable=false)
    private LocalDateTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private MarketStatus status = MarketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private Bet.BetSide winningSide;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private MarketCategory category = MarketCategory.OTHER;

    @Column(nullable=false, columnDefinition="decimal(10,2) default 0.00")
    private BigDecimal totalYesAmt = BigDecimal.ZERO;

    @Column(nullable=false, columnDefinition="decimal(10,2) default 0.00")
    private BigDecimal totalNoAmt = BigDecimal.ZERO;

    public enum MarketStatus {
        OPEN, CLOSED
    }

    public enum MarketCategory{
        ACADEMICS, SPORTS, CAMPUS_LIFE, OTHER
    }

    public Long getId() { 
        return id; 
    }

    public String getTitle() { 
        return title; 
    }
    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public Instant getCreatedAt(){
        return this.createdAt;
    }

    public LocalDateTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalDateTime closeTime) { 
        this.closeTime = closeTime; 
    }

    public MarketStatus getStatus() { 
        return status; 
    }
    public void setStatus(MarketStatus status) { 
        this.status = status; 
    }

    public Bet.BetSide getWinningSide() {
        return winningSide;
    }

    public void setWinningSide(Bet.BetSide winningSide) {
        this.winningSide = winningSide;
    }

    public MarketCategory getMarketCategory(){
        return this.category;
    }

    public void setMarketCategory(MarketCategory category){
        this.category = category;
    }

    public BigDecimal getTotalYesAmt(){
        return this.totalYesAmt;
    }

    public void setTotalYesAmt(BigDecimal totalYesAmt){
        this.totalYesAmt = this.totalYesAmt.add(totalYesAmt);
    }

    public BigDecimal getTotalNoAmt(){
        return this.totalNoAmt;
    }

    public void setTotalNoAmt(BigDecimal totalNoAmt){
        this.totalNoAmt = this.totalNoAmt.add(totalNoAmt);
    }

}