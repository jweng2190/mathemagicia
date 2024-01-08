package com.example.dto.message;

public class JoinMessage {
    private String type;
    private String playerUsername;
    private String gameId;
    private String joinType;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGameId() {
        return gameId;
    }

    /* @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    } */

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String player) {
        this.playerUsername = player;
    }

    public String getJoinType() {
        return this.joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

}
