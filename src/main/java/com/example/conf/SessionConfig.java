package com.example.conf;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.example.storage.GameSession;
import com.example.storage.UserSession;

@Configuration
public class SessionConfig {
    @Bean
    public UserSession myUserSession() {
        UserSession userSession = new UserSession();
        return userSession;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GameSession myGameSession() {
        GameSession gameSession = new GameSession();
        return gameSession;
    }
}
