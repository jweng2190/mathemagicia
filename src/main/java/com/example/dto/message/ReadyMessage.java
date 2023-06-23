package com.example.dto.message;

public class ReadyMessage {
    private String type;
    private String gameId;
    private String playerUsername;

    public ReadyMessage() {

    }

    public ReadyMessage(String type, String gameId, String playerUsername) {
        this.type = type;
        this.gameId = gameId;
        this.playerUsername = playerUsername;
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
    public String getPlayerUsername() {
        return playerUsername;
    }
    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }
}
