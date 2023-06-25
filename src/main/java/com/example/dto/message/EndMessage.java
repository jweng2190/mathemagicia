package com.example.dto.message;

public class EndMessage {
    private String type;
    private String gameId;
    private String playerUsername;


    public EndMessage(String type, String gameId, String playerType) {
        this.gameId = gameId;
        this.type = type;
        this.playerUsername = playerType;
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

    public void setPlayerUsername(String playerType) {
        this.playerUsername = playerType;
    }
}
