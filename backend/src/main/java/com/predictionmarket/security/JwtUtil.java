package com.predictionmarket.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
// Utility class for creating and validating JWT tokens, extracting user information from tokens, and managing the secret key used for signing tokens
@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String secretKey;  
    private SecretKey key;  

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);

    }

    /* Create a JWT token with the specified username and role */
    public String createToken(String username, String role){
        Instant now = Instant.now();
        
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(Duration.ofHours(8))))
            .signWith(key)
            .compact();
    }

    // Extract the username from a JWT token by parsing the token's claims and retrieving the subject claim
    public String getUsername(String token){
        Claims claim = this.getClaim(token);

        return claim.getSubject();
    }

    // Extract the user's role from a JWT token by parsing the token's claims and retrieving the "role" claim
    public String getRole(String token){
        Claims claim = this.getClaim(token);

        return claim.get("role", String.class);
    }

    // Helper method to parse a JWT token and extract its claims, throwing an exception if the token is invalid
    private Claims getClaim(String token){
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // Validate a JWT token by verifying its signature and checking its expiration date, returning true if the token is valid and false otherwise
    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
           return true;
        } catch (JwtException e) {
            return false;
        }
    }
}