package com.example.dto.message;

public class JoinMessage implements Message {
    private String type;
    private String gameId;
    private String playerUsername;
    private String content;

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getGameId() {
        return gameId;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String player) {
        this.playerUsername = player;
    }
}
