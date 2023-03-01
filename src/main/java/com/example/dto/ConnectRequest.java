package com.example.dto;

import com.example.model.User;

import lombok.Data;

@Data
public class ConnectRequest {
    private User player;
    private String gameId;
}
