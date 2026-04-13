package com.predictionmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.predictionmarket.model.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {
}