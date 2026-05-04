package com.predictionmarket.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/* Model class representing a market with various options */
@Entity
@Table(name = "markets")
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private LocalDateTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketStatus status = MarketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketCategory category = MarketCategory.OTHER;

    // All options for this market, like YES/NO or US/UK/Germany/Italy
    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("market")
    private List<MarketOption> options = new ArrayList<>();

    // Stored only in memory for API responses / convenience
    @Transient
    private Long winningOptionId;

    public enum MarketStatus {
        OPEN, CLOSED
    }

    public enum MarketCategory {
        ACADEMICS, SPORTS, CAMPUS_LIFE, OTHER
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public MarketStatus getStatus() {
        return status;
    }

    public void setStatus(MarketStatus status) {
        this.status = status;
    }

    public MarketCategory getMarketCategory() {
        return category;
    }

    public void setMarketCategory(MarketCategory category) {
        this.category = category;
    }

    public List<MarketOption> getOptions() {
        return options;
    }

    public void setOptions(List<MarketOption> options) {
        this.options = options;
    }

    public Long getWinningOptionId() {
        return winningOptionId;
    }

    public void setWinningOptionId(Long winningOptionId) {
        this.winningOptionId = winningOptionId;
    }
}