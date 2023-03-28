package com.example.model;

import lombok.Data;

@Data
public class Game {
    private String gameId;
    private User player1;
    private User player2;
    private GameStatus status;
    private User winner;

    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public User getPlayer1() {
        return this.player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return this.player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public User getWinner() {
        return this.winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

}
