package com.predictionmarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.predictionmarket.model.MarketOption;
// Repository interface for performing CRUD operations on MarketOption entities and finding options by market ID
public interface MarketOptionRepository extends JpaRepository<MarketOption, Long> {

    List<MarketOption> findByMarketId(Long marketId);
}