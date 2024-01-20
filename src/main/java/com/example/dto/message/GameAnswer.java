package com.example.dto.message;

public class GameAnswer {
    private int player1Score;
    private int player2Score;
    private int status;
    private int playerType;

    public GameAnswer() {
        
    }

    public GameAnswer(int player1Score, int player2Score, int status, int playerType) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.status = status;
        this.playerType = playerType;
    }

    public int getPlayer1Score() {
        return this.player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return this.player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPlayerType() {
        return this.playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }


}
