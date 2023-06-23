package com.example.dto.message;

import com.example.model.GameStatus;
import com.example.model.User;

public class GameMessage {
    private String type;
    private String gameId;
    private User player1;
    private User player2;
    private User winner;
    private String score1;
    private String score2;
    private GameStatus gameStatus;
    private String content;
    
    public GameMessage() {
    }

    public GameMessage(String type, String gameId, User player1Username, User player2Username, User winner,
            String score1, String score2, GameStatus gameStatus, String content) {
        this.type = type;
        this.gameId = gameId;
        this.player1 = player1Username;
        this.player2 = player2Username;
        this.winner = winner;
        this.score1 = score1;
        this.score2 = score2;
        this.gameStatus = gameStatus;
        this.content = content;
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

    public User getPlayer1() {
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
    }

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
