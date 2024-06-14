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
    @Query("SELECT game FROM Game game WHERE game.gameId = :gameId")
    Game getGameByGameId(@Param("gameId") String gameId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM game WHERE game_id = ?1", nativeQuery = true)
    void deleteByGameId(String gameId);

    @Query(value="SELECT * FROM game ORDER BY id DESC LIMIT 0, 1", nativeQuery=true)
    Game getRecentGame();
}
