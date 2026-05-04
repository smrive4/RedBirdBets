package com.predictionmarket.dto;

import java.math.BigDecimal;

// DTO for representing a leaderboard entry with user ID, username, and balance
public class LeaderboardEntryDTO {
    public Long id;
    public String username;
    public BigDecimal balance;

    public LeaderboardEntryDTO(Long id, String username, BigDecimal balance){
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

}