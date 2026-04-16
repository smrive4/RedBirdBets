package com.predictionmarket.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String role = "USER";

    @Column(name = "last_daily_claim_date")
    private LocalDate lastDailyClaimDate;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDate getLastDailyClaimDate() { return lastDailyClaimDate; }
    public void setLastDailyClaimDate(LocalDate lastDailyClaimDate) { this.lastDailyClaimDate = lastDailyClaimDate; }
}