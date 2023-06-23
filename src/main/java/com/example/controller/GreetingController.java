package com.example.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import com.example.Greeting;
import com.example.HelloMessage;

public class GreetingController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greet(HelloMessage greetMsg) {
        return new Greeting(greetMsg.getMessage());
    }
}
