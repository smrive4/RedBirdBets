package com.predictionmarket.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @Column(nullable=false)
    private LocalDateTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private MarketStatus status = MarketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private Bet.BetSide winningSide;

    public enum MarketStatus {
        OPEN, CLOSED
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
}