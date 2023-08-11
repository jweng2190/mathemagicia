package com.example.dto.message;

public class StatusMessage {
    private String gameId;
    private int numD1;
    private int numD2;
    private String status;
    /* private boolean player1Disconnect;
    private boolean player2Disconnect; */
    private String playerDisconnect;

    public StatusMessage(String gameId, int numD1, int numD2, String status) {
        this.gameId = gameId;
        this.numD1 = numD1;
        this.numD2 = numD2;
        this.status = status;
    }


    public StatusMessage(String gameId, int numD1, int numD2, String status, String playerDisconnect) {
        this.gameId = gameId;
        this.numD1 = numD1;
        this.numD2 = numD2;
        this.status = status;
        this.playerDisconnect = playerDisconnect;
    }


    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    public int getNumD1() {
        return numD1;
    }
    public void setNumD1(int numD1) {
        this.numD1 = numD1;
    }
    public int getNumD2() {
        return numD2;
    }
    public void setNumD2(int numD2) {
        this.numD2 = numD2;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    /* public boolean isPlayer1Disconnect() {
        return this.player1Disconnect;
    }

    public void setPlayer1Disconnect(boolean player1Disconnect) {
        this.player1Disconnect = player1Disconnect;
    }

    public boolean isPlayer2Disconnect() {
        return this.player2Disconnect;
    }

    public void setPlayer2Disconnect(boolean player2Disconnect) {
        this.player2Disconnect = player2Disconnect;
    } */

    public String getPlayerDisconnect() {
        return this.playerDisconnect;
    }

    public void setPlayerDisconnect(String playerDisconnect) {
        this.playerDisconnect = playerDisconnect;
    }
}
