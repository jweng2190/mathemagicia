package com.example.model;

import java.util.List;

import lombok.Data;

@Data
public class Game {
    private String gameId;
    private User player1;
    private User player2;
    private GameStatus status;
    private User winner;
    private boolean player1Joined;
    private boolean player2Joined;
    private boolean player1Ready;
    private boolean player2Ready;
    private List<Problem> problemSet;
    private Integer player1Score = 0;
    private Integer player2Score = 0;


    public Game() {
    }

    public Game(User player1, User player2) {
        this.player1 = player1;
        this.player1 = player2;
        player1Score = 0;
        player2Score = 0;
    }

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

    public boolean isPlayer1Joined() {
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
    }

    public boolean isPlayer1Ready() {
        return this.player1Ready;
    }

    public void setPlayer1Ready(boolean player1Ready) {
        this.player1Ready = player1Ready;
    }

    public boolean isPlayer2Ready() {
        return this.player2Ready;
    }

    public void setPlayer2Ready(boolean player2Ready) {
        this.player2Ready = player2Ready;
    }

    public List<Problem> getProblemSet() {
        return this.problemSet;
    }

    public void setProblemSet(List<Problem> problemSet) {
        this.problemSet = problemSet;
    }

    public Integer getPlayer1Score() {
        return this.player1Score;
    }

    public void setPlayer1Score(Integer player1Score) {
        this.player1Score = player1Score;
    }

    public Integer getPlayer2Score() {
        return this.player2Score;
    }

    public void setPlayer2Score(Integer player2Score) {
        this.player2Score = player2Score;
    }
}
