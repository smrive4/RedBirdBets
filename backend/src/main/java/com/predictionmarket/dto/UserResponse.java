package com.predictionmarket.dto;

import java.math.BigDecimal;

// DTO for representing a user response with ID, username, role, balance, and authentication token
public class UserResponse{
    private Long id;
    private String username;
    private String role;
    private BigDecimal balance;
    private String token;

    public UserResponse(Long id, String username, String role, BigDecimal balance, String token){
        this.id = id;
        this.username = username;
        this.role = role;
        this.balance = balance;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getToken() {
        return token;
    }

}