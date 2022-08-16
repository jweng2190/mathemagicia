package com.example.model;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER"),
    GUEST("GUEST");

    private String name;

    Role(String name) {
        this.name = name;
    }
}
