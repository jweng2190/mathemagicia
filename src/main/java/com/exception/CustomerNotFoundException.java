package com.exception;

public class CustomerNotFoundException extends Exception {
    private String message;

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
