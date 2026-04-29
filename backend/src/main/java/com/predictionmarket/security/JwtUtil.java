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

    public String getUsername(String token){
        Claims claim = this.getClaim(token);

        return claim.getSubject();
    }

    public String getRole(String token){
        Claims claim = this.getClaim(token);

        return claim.get("role", String.class);
    }

    private Claims getClaim(String token){
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
           return true;
        } catch (JwtException e) {
            return false;
        }
    }
}