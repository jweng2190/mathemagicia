package com.example.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "game")
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="game_id")
    private String gameId;

    @Column(name="game_difficulty")
    private String gameDifficulty;

    @Column(name="time_limit")
    private String timeLimit;

    @Column(name="player1_username")
    private String player1Username;

    @Column(name="player2_username")
    private String player2Username;

    @Enumerated(EnumType.STRING)
    @Column(name="game_status")
    private GameStatus status;

    private String winner;

    @ManyToMany(/* cascade = {
        CascadeType.PERSIST
    }, */ fetch = FetchType.EAGER)
    @JoinTable(
        name = "game_problem",
        joinColumns = @JoinColumn(name = "game_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "problem_id")
    )
    private List<Problem> problemSet;

    @Column(name="player1_score")
    private Integer player1Score = 0;

    @Column(name="player2_score")
    private Integer player2Score = 0;

    @Column(name="player1_xp")
    private Integer player1Xp = 0;

    @Column(name="player2_xp")
    private Integer player2Xp = 0;

    @Column(name="game_date", columnDefinition = "DATE")
    private LocalDate gameDate;

    @Column(name="player1_joined")
    private boolean player1Joined;

    @Column(name="player2_joined")
    private boolean player2Joined;

    @Transient
    private List<Integer> probStatus1;

    @Transient
    private List<Integer> probStatus2;

    @Transient
    private int numDisconnect1;
    
    @Transient
    private int numDisconnect2;

    @Transient
    private LocalTime timeRemaining;

    public Game() {
    }

    /* public Game(User player1, User player2) {
        this.player1 = player1;
        this.player1 = player2;
        player1Score = 0;
        player2Score = 0;
    } */

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameDifficulty() {
        return this.gameDifficulty;
    }

    public void setGameDifficulty(String gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    public String getTimeLimit() {
        return this.timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
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

    public String getPlayer1Username() {
        return this.player1Username;
    }

    public void setPlayer1Username(String player1Username) {
        this.player1Username = player1Username;
    }

    public String getPlayer2Username() {
        return this.player2Username;
    }

    public void setPlayer2Username(String player2Username) {
        this.player2Username = player2Username;
    }

    public LocalDate getGameDate() {
        return this.gameDate;
    }

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
    }

    public int getNumDisconnect1() {
        return this.numDisconnect1;
    }

    public void setNumDisconnect1(int numDisconnect1) {
        this.numDisconnect1 = numDisconnect1;
    }

    public int getNumDisconnect2() {
        return this.numDisconnect2;
    }

    public void setNumDisconnect2(int numDisconnect2) {
        this.numDisconnect2 = numDisconnect2;
    }

    public LocalTime getTimeRemaining() {
        return this.timeRemaining;
    }

    public void setTimeRemaining(LocalTime timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public Integer getPlayer1Xp() {
        return this.player1Xp;
    }

    public void setPlayer1Xp(Integer player1Xp) {
        this.player1Xp = player1Xp;
    }

    public Integer getPlayer2Xp() {
        return this.player2Xp;
    }

    public void setPlayer2Xp(Integer player2Xp) {
        this.player2Xp = player2Xp;
    }

    public List<Integer> getProbStatus1() {
        return this.probStatus1;
    }

    public void setProbStatus1(List<Integer> probStatus1) {
        this.probStatus1 = probStatus1;
    }

    public List<Integer> getProbStatus2() {
        return this.probStatus2;
    }

    public void setProbStatus2(List<Integer> probStatus2) {
        this.probStatus2 = probStatus2;
    }

}
