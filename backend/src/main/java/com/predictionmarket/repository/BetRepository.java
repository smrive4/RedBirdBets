package com.predictionmarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.predictionmarket.dto.LeaderboardEntryDTO;
import com.predictionmarket.model.Bet;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUserId(Long userId);
    List<Bet> findByMarketId(Long marketId);

    @Query("SELECT new com.predictionmarket.dto.LeaderboardEntryDTO(b.user.id, b.user.username, SUM(b.payout)) FROM Bet b WHERE b.won = true GROUP BY b.user.id ORDER BY SUM(payout) DESC")
    List<LeaderboardEntryDTO> getWinningsByDesc();
}