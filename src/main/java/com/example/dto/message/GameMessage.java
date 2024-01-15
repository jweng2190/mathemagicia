package com.example.dto.message;

import java.util.List;

import com.example.model.GameStatus;
import com.example.model.Problem;
import com.example.model.User;

public class GameMessage {
    private String type;
    private String gameId;
    private int score1;
    private int score2;
    private String winner;
    private GameStatus status;

    public GameMessage() {
    }

    public GameMessage(String type, String gameId, int score1, int score2,
         GameStatus status) {
        this.type = type;
        this.gameId = gameId;
        this.score1 = score1;
        this.score2 = score2;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getWinner() {
        return this.winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    /* public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    } */

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public GameStatus getGameStatus() {
        return status;
    }

    public void setGameStatus(GameStatus status) {
        this.status = status;
    }

    /* public boolean isPlayer1Joined() {
        return this.player1Joined;
    }

    public void setPlayer1Joined(boolean player1Joined) {
        this.player1Joined = player1Joined;
    }

    public boolean isPlayer2Joined() {
        return this.player2Joined;
    }

    public void setPlayer2Joined(boolean player2Joined) {
        this.player2Joined = player2Joined;
    } */
}
