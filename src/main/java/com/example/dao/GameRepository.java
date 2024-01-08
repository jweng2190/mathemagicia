package com.example.dao;

import javax.transaction.Transactional;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Game;
import com.example.model.GameStatus;
import com.example.model.User;
import com.example.service.GameService;
import java.util.List;



@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO game (id, game_id, player1_username, player2_username, game_status, " + 
    "winner, player1_score, player2_score) VALUES (null, :gameId, :player1Username, :player2Username, " + 
    ":status, :winner, :player1Score, :player2Score)", nativeQuery = true)
    public void insertGame(@Param("gameId") String gameId, 
    @Param("player1Username") String player1Username, @Param("player2Username") String player2Username,
    @Param("status") String status, @Param("winner") String winner, @Param("player1Score") 
    int player1Score, @Param("player2Score") int player2Score);

    @Query("SELECT game FROM Game game WHERE game.gameId = :gameId")
    Game getGameByGameId(@Param("gameId") String gameId);

    @Query(value="SELECT * FROM game ORDER BY id DESC LIMIT 0, 1", nativeQuery=true)
    Game getRecentGame();
}
