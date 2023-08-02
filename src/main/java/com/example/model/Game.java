package com.example.model;

import java.time.LocalDate;
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

    @Column(name="player1_username")
    private String player1Username;

    @Column(name="player2_username")
    private String player2Username;

    /* @Transient
    private User player1;

    @Transient
    private User player2; */

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

    @Column(name="game_date", columnDefinition = "DATE")
    private LocalDate gameDate;

    @Column(name="player1_joined")
    private boolean player1Joined;

    @Column(name="player2_joined")
    private boolean player2Joined;

    @Column(name="player1_ready")
    private boolean player1Ready;

    @Column(name="player2_ready")
    private boolean player2Ready;

    @Column(name="player1_rematch")
    private boolean player1Rematch;

    @Column(name="player2_rematch")
    private boolean player2Rematch;

    @Transient
    private int numDisconnect1;
    
    @Transient
    private int numDisconnect2;


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

    /* public User getPlayer1() {
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
    } */

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

    public boolean isPlayer1Ready() {
        return this.player1Ready;
    }

    public void setPlayer1Ready(boolean player1Ready) {
        this.player1Ready = player1Ready;
    }

    public boolean isPlayer2Ready() {
        return this.player2Ready;
    }

    public boolean isPlayer1Rematch() {
        return this.player1Rematch;
    }

    public void setPlayer1Rematch(boolean player1Rematch) {
        this.player1Rematch = player1Rematch;
    }

    public boolean isPlayer2Rematch() {
        return this.player2Rematch;
    }

    public void setPlayer2Rematch(boolean player2Rematch) {
        this.player2Rematch = player2Rematch;
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
}
